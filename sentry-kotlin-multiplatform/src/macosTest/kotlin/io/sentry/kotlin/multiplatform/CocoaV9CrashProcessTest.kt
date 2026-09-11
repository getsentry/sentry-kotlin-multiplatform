package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentryDependencyContainer
import cocoapods.Sentry.SentrySDK
import cocoapods.Sentry.flush
import cocoapods.Sentry.startSession
import kotlinx.cinterop.toKString
import platform.Foundation.NSDate
import platform.Foundation.NSRunLoop
import platform.Foundation.dateWithTimeIntervalSinceNow
import platform.Foundation.runUntilDate
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fputs
import platform.posix.getenv
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/** Opt-in subprocess fixtures; run with scripts/test-cocoa-crash-relaunch.py. */
class CocoaV9CrashProcessTest {
    @Test
    fun crash() {
        if (environment("PHASE") != "crash") return
        validateEnvironment()
        val marker = requiredEnvironment("MARKER")
        val token = requiredEnvironment("TOKEN")
        setUnhandledExceptionHook { appendMarker(marker, "previous:$token") }
        start()
        // Exercise hook ownership across close/restart before traversing the real fatal hook.
        Sentry.close()
        start()
        Sentry.configureScope { it.setTag("cocoa_crash_fixture", token) }
        val reporter = SentryDependencyContainer.sharedInstance().crashReporter()
        assertNotNull(reporter.uncaughtExceptionHandler())
        if (requiredEnvironment("SCENARIO") == "fallback") {
            reporter.setUncaughtExceptionHandler(null)
            assertNull(reporter.uncaughtExceptionHandler())
        }
        if (environment("SESSIONS") == "1") {
            // A command-line executable has no application activation notification.
            SentrySDK.startSession()
            SentrySDK.flush(5.0)
        }
        appendMarker(marker, "armed:$token")
        processUnhandledException(IllegalStateException("outer:$token", IllegalArgumentException("cause:$token")))
        error("processUnhandledException unexpectedly returned")
    }

    @Test
    fun recover() {
        if (environment("PHASE") != "recover") return
        validateEnvironment()
        start()
        try {
            // Crash report conversion is asynchronous; also service Cocoa's main queue.
            NSRunLoop.currentRunLoop.runUntilDate(NSDate.dateWithTimeIntervalSinceNow(5.0))
            SentrySDK.flush(10.0)
        } finally {
            Sentry.close()
        }
        appendMarker(requiredEnvironment("MARKER"), "recovered:${requiredEnvironment("TOKEN")}")
    }

    private fun start() {
        Sentry.initWithPlatformOptions {
            it.setDsn(requiredEnvironment("DSN"))
            it.setCacheDirectoryPath(requiredEnvironment("CACHE"))
            it.setReleaseName("cocoa-crash-fixture@1")
            it.setEnableAutoSessionTracking(false)
            it.setEnableAppHangTracking(false)
            it.setEnableSpotlight(false)
        }
    }

    private fun validateEnvironment() {
        require(requiredEnvironment("SCENARIO") in setOf("native", "fallback"))
        require(Regex("http://public@127\\.0\\.0\\.1:[0-9]+/1").matches(requiredEnvironment("DSN")))
        require(requiredEnvironment("CACHE").startsWith("/"))
        require(requiredEnvironment("MARKER").startsWith("/"))
        require(Regex("[a-zA-Z0-9-]+").matches(requiredEnvironment("TOKEN")))
    }

    private fun appendMarker(
        path: String,
        value: String,
    ) {
        val file = checkNotNull(fopen(path, "a")) { "Cannot open fixture marker: $path" }
        try {
            check(fputs("$value\n", file) >= 0)
        } finally {
            check(fclose(file) == 0)
        }
    }

    private fun environment(name: String): String? = getenv("SENTRY_COCOA_CRASH_$name")?.toKString()

    private fun requiredEnvironment(name: String): String = checkNotNull(environment(name)) { "Missing fixture $name" }
}
