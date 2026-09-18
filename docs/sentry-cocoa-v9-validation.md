# Sentry Cocoa 9.28.0 validation

Branch: `codex/sentry-cocoa-v9-migration`, based directly on PR #561 commit `9a212e3b6a8cdb54f1bba328d7e00342c46ca047`. See the [migration plan](sentry-cocoa-v9-migration-plan.md) and [API investigation](sentry-cocoa-v9-investigation.md).

Status: implementation, aggregate validation and refreshed external-consumer validation complete.

## Implementation

- **Build/plugin:** pin Cocoa 9.28.0 with Kotlin 2.2.21 and spm4Kmp 1.9.5; retain the Sentry product and `cocoapods.Sentry` binding namespace. Set minimums to iOS/tvOS 15, macOS 12 and watchOS 9, preserving higher consumer values and user-owned Swift packages. Publish watchosArm32 as a no-op stub while retaining Cocoa-backed arm64_32 support. Use the SPM sample for active validation. The plugin supplies the watchOS simulator slice-copy fix for auto-installed packages.
- **Adapters:** use generated Cocoa getters/setters and SDK extension imports. Move logs enablement to the top-level option and use `SentryAttribute`, retaining typed values, callback mutation, deletion and filtering. Convert feedback to `SentryFeedback` with `comments.orEmpty()`, source `custom`, nullable contact fields and the original `associatedEventId`.
- **Native ABI:** align remaining private declarations with Cocoa 9 and use generated container/reporter/provider bindings. Remove obsolete async fields and the `symbolicate` pointer from `SentryCrashStackCursor`; stop including `SentryHook.h`. Build the stacktrace before freeing its malloc-backed address array.
- **Crash behavior:** change only the C++ bit through the active monitor mask; preserve close/restart behavior and previous Kotlin hook chaining without repeated wrapping. Fallback envelope preparation respects a filtered null result, enriches from the live scope, then marks the prepared event fatal before persistence. Retrieve cached debug images for exception and retained thread frames.
- **Duplicate suppression:** filter the original tagged native crash before invoking a user `beforeSend` callback, so a replacement event cannot erase the tag and revive a duplicate. The regression is covered by the passing final macOS suite.
- **Tests/docs:** cover options, logs, feedback serialization, native exception/envelope conversion, monitor lifecycle and crash/relaunch sessions. Plugin architecture tests accept versioned local framework fixtures and use bounded network connection/read timeouts. Review these as three chunks: build/plugin; adapters/crash; tests/docs.

No upstream Cocoa patch is required by the implemented and validated paths. Private API declarations remain tied to **9.28.0** and require review on future upgrades.

## watchosArm32 compatibility follow-up

The legacy `watchosArm32` target now uses `commonStub`, outside the Cocoa-backed Apple source hierarchy. It publishes a klib with no Cocoa interop or framework dependency. Capture calls return an empty event ID, `isEnabled()` stays false, and initialization, scope, feedback and logging use the existing no-op implementations. Auto-installation warns and skips this target; fallback framework linking skips it as well, including custom target names. This is compile compatibility, not restored monitoring support.

Cocoa raised its minimum to watchOS 9 in [9.25.0 / PR #8595](https://github.com/getsentry/sentry-cocoa/pull/8595). The [commit](https://github.com/getsentry/sentry-cocoa/commit/7511ca2bad378e96029f7ba7c29aa3aa3f6cec00) explains that watchOS 9 rejects armv7k binaries and IR.

Follow-up checks: `spotlessApply`, `detekt`, `apiCheck`, and the full `build` passed (`/private/tmp/kmp-watch32-gates-final.log`), with 2,248 SDK test executions and no failures or skips. The plugin suite passed all 149 tests (`/private/tmp/kmp-watch32-plugin.log`). The compiled watchosArm32 klib manifest declares only `depends=stdlib`. An initial overlapping build/publication run damaged generated commonizer outputs; regenerating that metadata and running the checks sequentially resolved the failure.

The refreshed isolated publications also passed: SDK/root metadata in `/private/tmp/kmp-watch32-publish-sdk-final.log` and plugin in `/private/tmp/kmp-watch32-publish-plugin.log`. A separate consumer declared `watchosArm32("legacyWatch")` alongside Cocoa-backed targets, resolved the SDK/plugin exclusively from the temporary Maven repository, and passed `compileKotlinLegacyWatch linkDebugFrameworkLegacyWatch` (`/private/tmp/kmp-watch32-consumer.log`). No Cocoa or Swift compilation/interoperability task ran for the stub. This verifies publication, compilation and framework linking; no armv7k device runtime test was performed.

## Results

| Check | Confirmed result | Evidence |
| --- | --- | --- |
| Final aggregate after callback fix | `make compile` passed API checks, detekt, SDK build/tests and Xcode simulator sample; exit 0, ending `BUILD SUCCEEDED` | `/private/tmp/kmp-v9-complete.log` |
| Final SDK executions | **2,248 passes**: iOS 347, macOS 349, tvOS 347, watchOS 347, JVM 282, Android debug 288, Android release 288 | Same aggregate log |
| Final matrix and callback regression | Formatting/detekt and six remaining device/x64 test-binary links passed; full macOS suite **349 tests, 0 failures/errors/skips** | `/private/tmp/kmp-v9-final-matrix.log`; `sentry-kotlin-multiplatform/build/test-results/macosArm64Test/TEST-*.xml` |
| Apple compilation/link coverage | All 11 retained targets compiled and linked across the aggregate and final matrix runs | iOS arm64/simulatorArm64/x64; macOS arm64/x64; tvOS arm64/simulatorArm64/x64; watchOS arm64/simulatorArm64/x64 |
| Plugin suite and detekt | **148 tests, 0 skipped, 0 failed** | `/private/tmp/kmp-v9-plugin-complete.log`; static/dynamic fixtures for 8.37.0, 8.38.0, 8.58.2 and 9.28.0 checked against `CFBundleShortVersionString` |
| External published consumer | Final SDK/plugin publications refreshed; iOS/watchOS simulator dynamic frameworks linked; macOS startup, metadata, capture/filtering and shutdown passed | `/private/tmp/kmp-v9-consumer-complete.log`; publication logs `/private/tmp/kmp-v9-publish-sdk-complete.log` and `/private/tmp/kmp-v9-publish-plugin-complete.log` |
| macOS crash/relaunch | Native-handler and fallback scenarios passed after the callback guard, including crashed-session accounting; each delivered one event and invoked the previous hook once | `/private/var/folders/8b/kysc2h494gj4lylw97khbtzh0000gn/T/cocoa-crash-relaunch-k6df8418/results.json` |

The stricter consumer run denied reads of the SDK checkout's `build/spmKmpPlugin` directory and reran all consumer Kotlin compilations, native links and the macOS test. Swift compilation was excluded because nested SwiftPM sandboxing is unsupported; it reused previously resolved/built **consumer** frameworks. This proves the restricted publication/link boundary, not a clean sandboxed SwiftPM build. The consumer resolves SDK/plugin 0.27.0 exclusively from an isolated Maven repository, without project dependencies, composite substitution or mavenLocal fallback. Its startup test uses a loopback DSN and drops every event with `beforeSend`; the crash harness uses a loopback receiver.

## Reproduction

From the checkout root on macOS with Xcode and JDK 17, run sequentially:

```sh
export JAVA_HOME="$(/usr/libexec/java_home -v 17)"
export PATH="$JAVA_HOME/bin:$PATH"
export CI=false
export SENTRY_SKIP_UPLOAD=1
./gradlew spotlessApply
SENTRY_COCOA_TEST_FRAMEWORKS=/private/tmp/kmp-v9-framework-fixtures \
  ./gradlew -p sentry-kotlin-multiplatform-gradle-plugin detekt test
make compile
./gradlew :sentry-kotlin-multiplatform:linkDebugTestMacosArm64
python3 scripts/test-cocoa-crash-relaunch.py --check-sessions
sh /private/tmp/kmp-v9-consumer/publish-sdk.sh
sh /private/tmp/kmp-v9-consumer/publish-plugin.sh
sh /private/tmp/kmp-v9-consumer/build-consumer.sh
```

Local architecture fixtures use `<root>/<version>/Sentry.xcframework` and `Sentry-Dynamic.xcframework`; verify bundle versions before reuse. The optional fixture input avoids downloads. The external-consumer scripts publish `kotlinMultiplatform`, `iosSimulatorArm64`, `macosArm64`, `watchosSimulatorArm64`, `pluginMaven` and `sentryPluginPluginMarkerMaven` to their file repository, then link both frameworks and run `macosArm64Test`. The scripts reproduce ordinary consumer validation; the stricter sandbox run additionally requires its read-denial policy and Swift-task exclusions.

## Coverage limits

Real-device execution and x64 runtime tests were not performed; compiled/linked binaries and host-skipped tests are not runtime passes. `CI=false` enables the configured `*E2E*` exclusion, so service E2E tests were not run. `SENTRY_SKIP_UPLOAD=1` skips sample symbol uploads. macOS crash/relaunch results do not establish equivalent fatal-process behavior on every Apple OS. Local log paths are evidence from this workspace, not durable CI artifacts.
