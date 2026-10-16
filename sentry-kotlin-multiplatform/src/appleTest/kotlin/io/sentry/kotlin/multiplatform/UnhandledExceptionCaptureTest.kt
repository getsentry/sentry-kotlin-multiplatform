package io.sentry.kotlin.multiplatform

import Internal.Sentry.SentryCrashMonitorTypeNSException
import Internal.Sentry.SentrySDKInternal
import Internal.Sentry.sentrycrash_setMonitoring
import Internal.Sentry.sentrycrashcm_getActiveMonitors
import cocoapods.Sentry.SentryDependencyContainer
import cocoapods.Sentry.SentryEvent
import cocoapods.Sentry.SentryException
import cocoapods.Sentry.SentryFileContents
import io.sentry.kotlin.multiplatform.nsexception.KOTLIN_CRASH_TAG
import io.sentry.kotlin.multiplatform.nsexception.captureUnhandledException
import platform.Foundation.NSException
import platform.Foundation.NSNumber
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSUUID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

class UnhandledExceptionCaptureTest {
    @BeforeTest
    @AfterTest
    fun close() {
        Sentry.close()
    }

    private fun start(
        sampleRate: Double = 1.0,
        beforeSend: (SentryEvent?) -> SentryEvent? = { it },
    ) {
        Sentry.initWithPlatformOptions {
            it.setDsn("http://public@127.0.0.1:9/1")
            it.setCacheDirectoryPath(NSTemporaryDirectory() + NSUUID().UUIDString)
            it.setEnableAutoSessionTracking(false)
            it.setEnableAppHangTracking(false)
            it.setSampleRate(NSNumber(sampleRate))
            it.setBeforeSend(beforeSend)
        }
    }

    private fun envelopePaths(): Set<String> =
        assertNotNull(SentryDependencyContainer.sharedInstance().fileManager())
            .getAllEnvelopes()
            .map { (it as SentryFileContents).path() }
            .toSet()

    private fun disableNativeExceptionMonitor() {
        sentrycrash_setMonitoring(sentrycrashcm_getActiveMonitors() and SentryCrashMonitorTypeNSException.inv())
        assertEquals(0u, sentrycrashcm_getActiveMonitors() and SentryCrashMonitorTypeNSException)
    }

    private fun crashTag(): Any? =
        SentrySDKInternal
            .currentHub()
            .scope.tagDictionary
            ?.objectForKey(KOTLIN_CRASH_TAG)

    @Test
    fun `active native monitor receives the converted exception without storing a fallback`() {
        start()
        assertTrue(sentrycrashcm_getActiveMonitors() and SentryCrashMonitorTypeNSException != 0u)
        val before = envelopePaths()
        val exceptions = mutableListOf<NSException>()

        // Intercept the fatal native call so the test process can continue.
        IllegalStateException("outer", IllegalArgumentException("inner")).captureUnhandledException {
            exceptions.add(it)
        }

        assertEquals(1, exceptions.size)
        assertTrue(assertNotNull(exceptions.single().reason).contains("outer"))
        assertTrue(assertNotNull(exceptions.single().reason).contains("inner"))
        assertEquals(before, envelopePaths())
        assertNull(crashTag())
    }

    @Test
    fun `inactive native monitor persists the enriched fatal event and tags the termination crash`() {
        var captured: SentryEvent? = null
        start {
            captured = it
            it
        }
        disableNativeExceptionMonitor()
        Sentry.configureScope { it.setTag("fallback", "scope") }
        val before = envelopePaths()

        IllegalStateException("fallback crash").captureUnhandledException { fail("Native capture must not run") }

        val event = assertNotNull(captured)
        assertEquals("scope", event.tags?.get("fallback"))
        assertEquals(false, (event.exceptions?.single() as SentryException).mechanism?.handled?.boolValue)
        assertTrue((event as Internal.Sentry.SentryEvent).isFatalEvent)
        assertEquals(1, (envelopePaths() - before).size)
        assertEquals(KOTLIN_CRASH_TAG, crashTag())
    }

    @Test
    fun `fallback does not persist events dropped by beforeSend`() {
        var called = false
        start {
            called = true
            null
        }
        disableNativeExceptionMonitor()
        val before = envelopePaths()

        IllegalStateException("dropped").captureUnhandledException { fail("Native capture must not run") }

        assertTrue(called)
        assertEquals(before, envelopePaths())
        assertEquals(KOTLIN_CRASH_TAG, crashTag())
    }

    @Test
    fun `fallback does not persist sampled out events or call beforeSend`() {
        var called = false
        start(sampleRate = 0.0) {
            called = true
            it
        }
        disableNativeExceptionMonitor()
        val before = envelopePaths()

        IllegalStateException("sampled out").captureUnhandledException { fail("Native capture must not run") }

        assertFalse(called)
        assertEquals(before, envelopePaths())
        assertEquals(KOTLIN_CRASH_TAG, crashTag())
    }
}
