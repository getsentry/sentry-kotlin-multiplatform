#!/usr/bin/env python3
"""Validate an already-built macOS test executable; never invokes Gradle.

Usage: python3 scripts/test-cocoa-crash-relaunch.py [--check-sessions]
Build macosArm64 debugTest with the main build coordinator first. Each scenario
crashes once (after an SDK restart), then relaunches twice with the same DSN/cache.
Only 127.0.0.1 receives traffic. Artifacts are retained in a new temporary directory
or a new --output directory. --check-sessions additionally requires one crashed
session matching the explicitly started session (auto session tracking stays off).
"""

import argparse
import gzip
import io
import json
import os
from pathlib import Path
import resource
import subprocess
import sys
import tempfile
import threading
import uuid
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer


TEST_CLASS = "io.sentry.kotlin.multiplatform.CocoaV9CrashProcessTest"
PREFIX = "SENTRY_COCOA_CRASH_"
ROOT = Path(__file__).resolve().parents[1]


def envelope_items(body):
    """Honor byte lengths: attachments can contain arbitrary bytes/newlines."""
    stream = io.BytesIO(body)
    header = json.loads(stream.readline())
    items = []
    while True:
        line = stream.readline()
        if not line:
            break
        if not line.strip():
            continue
        item = json.loads(line)
        length = item.get("length")
        if length is None:
            payload = stream.readline().rstrip(b"\n")
        else:
            if not isinstance(length, int) or length < 0:
                raise ValueError("Invalid envelope item length")
            payload = stream.read(length)
            if len(payload) != length:
                raise ValueError("Truncated envelope item")
        kind = item.get("type")
        if kind in {"event", "session", "sessions"}:
            items.append({"type": kind, "payload": json.loads(payload)})
    return header, items


class Receiver(ThreadingHTTPServer):
    daemon_threads = True

    def __init__(self, output):
        super().__init__(("127.0.0.1", 0), Handler)
        self.output = output
        self.records = []
        self.errors = []
        self.phase = "crash"
        self.lock = threading.Lock()


class Handler(BaseHTTPRequestHandler):
    def do_POST(self):
        try:
            if self.path != "/api/1/envelope/":
                raise ValueError(f"Unexpected request path: {self.path}")
            length = int(self.headers["Content-Length"])
            if not 0 < length <= 32 * 1024 * 1024:
                raise ValueError("Invalid request size")
            body = self.rfile.read(length)
            encoding = self.headers.get("Content-Encoding", "identity").lower()
            if encoding == "gzip":
                body = gzip.decompress(body)
            elif encoding != "identity":
                raise ValueError(f"Unsupported Content-Encoding: {encoding}")
            header, items = envelope_items(body)
            with self.server.lock:
                index = len(self.server.records)
                name = f"envelope-{index:03d}.bin"
                (self.server.output / name).write_bytes(body)
                self.server.records.append({
                    "phase": self.server.phase, "file": name,
                    "header": header, "items": items,
                })
            response = b"{}"
            self.send_response(200)
            self.send_header("Content-Type", "application/json")
            self.send_header("Content-Length", str(len(response)))
            self.end_headers()
            self.wfile.write(response)
        except Exception as error:
            with self.server.lock:
                self.server.errors.append(str(error))
            self.send_error(400, "Invalid fixture envelope")

    def log_message(self, *_args):
        pass


def no_core_dump():
    resource.setrlimit(resource.RLIMIT_CORE, (0, 0))


def run_child(binary, method, env, output, phase, timeout):
    with (output / f"{phase}.log").open("wb") as log:
        # Changes apply only to the child. Avoid proxying even a loopback request.
        result = subprocess.run(
            [str(binary), f"--ktest_filter={TEST_CLASS}.{method}"],
            env=env, cwd=output, stdout=log, stderr=subprocess.STDOUT,
            timeout=timeout, preexec_fn=no_core_dump,
        )
    return {"phase": phase, "returncode": result.returncode}


def require(condition, message):
    if not condition:
        raise AssertionError(message)


def validate(records, marker, token, sessions):
    lines = marker.read_text().splitlines()
    require(lines.count(f"armed:{token}") == 1, "Crash hook was not armed exactly once")
    require(lines.count(f"previous:{token}") == 1, "Previous hook must run exactly once")
    require(lines.count(f"recovered:{token}") == 2, "Both recovery tests must finish")
    events = [(record["phase"], item["payload"])
              for record in records for item in record["items"] if item["type"] == "event"]
    require(len(events) == 1, f"Expected exactly one event across all launches, got {len(events)}")
    phase, event = events[0]
    require(phase == "recover-1", f"Fatal event delivered during {phase}, not first relaunch")
    require(bool(event.get("event_id")), "Missing event ID")
    require(event.get("level") == "fatal", "Expected fatal level")
    require(event.get("tags", {}).get("cocoa_crash_fixture") == token, "Scope tag lost")
    exceptions = event.get("exception", {}).get("values", [])
    values = "\n".join(str(exc.get("value", "")) for exc in exceptions)
    require(f"outer:{token}" in values, "Outer exception missing")
    # Native reports merge the cause into the reason; fallback keeps separate values.
    require(f"cause:{token}" in values, "Cause marker missing")
    require(any(exc.get("mechanism", {}).get("handled") is False for exc in exceptions),
            "Unhandled mechanism missing")
    session_payloads = [item["payload"] for record in records for item in record["items"]
                        if item["type"] == "session"]
    if sessions:
        started = {item["sid"] for item in session_payloads if item.get("init") is True}
        crashed = [item for item in session_payloads if item.get("status") == "crashed"]
        require(len(started) == 1, f"Expected one started session, got {len(started)}")
        require(len(crashed) == 1, f"Expected one crashed session, got {len(crashed)}")
        require(crashed[0].get("sid") in started, "Crashed session ID differs from started session")
        initial = next(item for item in session_payloads if item.get("init") is True)
        require(initial.get("status") == "ok", "Initial session must be healthy")
        require(crashed[0].get("seq", 0) > initial.get("seq", 0),
                "Crashed session sequence did not advance")
        # Cocoa 9.28.0 SentrySession.endCrashed changes status/sequence, not errors.
        # SentryCrashIntegrationSessionHandler uses that operation on relaunch;
        # incrementErrors is a separate operation for captured in-process errors.
        require(crashed[0].get("errors") == initial.get("errors") == 0,
                "Unexpected in-process session errors in the fatal-only fixture")
        require(any(record["phase"] == "recover-1"
                    and any(item["type"] == "event"
                            and item["payload"].get("event_id") == event["event_id"]
                            for item in record["items"])
                    and any(item["type"] == "session" and item["payload"] == crashed[0]
                            for item in record["items"])
                    for record in records),
                "Crashed session must accompany the fatal event on first relaunch")
    return {"event_id": event["event_id"], "events": len(events),
            "previous_hook_calls": 1, "session_validation": sessions,
            "sessions": session_payloads}


def scenario(binary, output, name, check_sessions, timeout):
    output.mkdir()
    cache = output / "cache"
    cache.mkdir()
    marker = output / "markers.txt"
    token = f"{name}-{uuid.uuid4().hex}"
    receiver = Receiver(output)
    worker = threading.Thread(target=receiver.serve_forever, daemon=True)
    worker.start()
    env = {key: value for key, value in os.environ.items()
           if not key.startswith(PREFIX) and key.lower() not in
           {"http_proxy", "https_proxy", "all_proxy", "no_proxy"}}
    env["NO_PROXY"] = "127.0.0.1,localhost"
    env.update({PREFIX + key: value for key, value in {
        "PHASE": "crash", "SCENARIO": name, "TOKEN": token,
        "DSN": f"http://public@127.0.0.1:{receiver.server_port}/1",
        "CACHE": str(cache), "MARKER": str(marker),
        "SESSIONS": "1" if check_sessions else "0",
    }.items()})
    result = {"scenario": name, "token": token, "processes": [], "passed": False}
    try:
        crash = run_child(binary, "crash", env, output, "crash", timeout)
        result["processes"].append(crash)
        require(crash["returncode"] < 0, f"Expected fatal signal, got {crash['returncode']}")
        require(marker.exists(), "Fixture did not write marker; rebuild test executable")
        require(marker.read_text().splitlines() == [f"armed:{token}", f"previous:{token}"],
                "Fatal hook did not chain exactly once")
        result["persisted_after_crash"] = [str(path.relative_to(cache))
                                            for path in cache.rglob("*") if path.is_file()]
        require(result["persisted_after_crash"], "Cocoa cache empty after crash")
        env[PREFIX + "PHASE"] = "recover"
        for phase in ("recover-1", "recover-2"):
            with receiver.lock:
                receiver.phase = phase
            recovery = run_child(binary, "recover", env, output, phase, timeout)
            result["processes"].append(recovery)
            require(recovery["returncode"] == 0, f"{phase} failed: {recovery['returncode']}")
        require(not receiver.errors, f"Receiver errors: {receiver.errors}")
        result.update(validate(receiver.records, marker, token, check_sessions))
        result["passed"] = True
    except Exception as error:
        result["error"] = str(error)
    finally:
        receiver.shutdown()
        receiver.server_close()
        worker.join()
        result["envelopes"] = receiver.records
        result["receiver_errors"] = receiver.errors
        (output / "result.json").write_text(json.dumps(result, indent=2) + "\n")
    return result


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--binary", type=Path, default=ROOT / "sentry-kotlin-multiplatform"
                        / "build/bin/macosArm64/debugTest/test.kexe")
    parser.add_argument("--output", type=Path, help="New directory for retained artifacts")
    parser.add_argument("--check-sessions", action="store_true")
    parser.add_argument("--timeout", type=float, default=40, help="Seconds per child (default: 40)")
    args = parser.parse_args()
    binary = args.binary.resolve()
    require(args.timeout > 20, "Timeout must exceed the recovery/flush window (20 seconds)")
    require(binary.is_file(), f"Build test executable first: {binary}")
    # A filter matching zero tests can succeed. Refuse stale binaries before crashing.
    listing = subprocess.run([str(binary), "--ktest_list_tests"], capture_output=True,
                             text=True, timeout=15, check=True).stdout
    require("CocoaV9CrashProcessTest" in listing and "crash" in listing and "recover" in listing,
            "Executable lacks crash fixtures; ask the build coordinator to rebuild it")
    if args.output:
        output = args.output.resolve()
        output.mkdir(parents=True, exist_ok=False)
    else:
        output = Path(tempfile.mkdtemp(prefix="cocoa-crash-relaunch-")).resolve()
    print(f"Artifacts: {output}", flush=True)
    results = []
    for name in ("native", "fallback"):
        result = scenario(binary, output / name, name, args.check_sessions, args.timeout)
        results.append(result)
        print(f"{name}: {'PASS' if result['passed'] else 'FAIL: ' + result['error']}", flush=True)
    (output / "results.json").write_text(json.dumps(results, indent=2) + "\n")
    return 0 if all(result["passed"] for result in results) else 1


if __name__ == "__main__":
    try:
        sys.exit(main())
    except (AssertionError, OSError, subprocess.SubprocessError) as error:
        print(f"ERROR: {error}", file=sys.stderr)
        sys.exit(1)
