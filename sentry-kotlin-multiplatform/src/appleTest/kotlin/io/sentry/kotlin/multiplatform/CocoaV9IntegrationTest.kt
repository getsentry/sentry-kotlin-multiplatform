package io.sentry.kotlin.multiplatform

import Internal.Sentry.SentryCrashMonitorTypeCPPException
import Internal.Sentry.sentrycrashcm_getActiveMonitors
import cocoapods.Sentry.SentryDebugMeta
import cocoapods.Sentry.SentryDependencyContainer
import cocoapods.Sentry.SentryEvent
import cocoapods.Sentry.SentryException
import cocoapods.Sentry.SentryFrame
import cocoapods.Sentry.SentryThread
import io.sentry.kotlin.multiplatform.nsexception.KOTLIN_CRASH_TAG
import io.sentry.kotlin.multiplatform.nsexception.asSentryEnvelope
import io.sentry.kotlin.multiplatform.nsexception.asSentryEvent
import kotlin.native.OsFamily
import kotlin.native.Platform
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CocoaV9IntegrationTest {
    @BeforeTest
    fun reset() {
        Sentry.close()
    }

    @AfterTest
    fun close() {
        Sentry.close()
    }

    private fun start(beforeSend: (SentryEvent?) -> SentryEvent? = { null }) {
        Sentry.initWithPlatformOptions {
            // Every test drops outgoing events. The local endpoint is also deliberately unreachable.
            it.setDsn("http://public@127.0.0.1:9/1")
            it.setEnableAutoSessionTracking(false)
            it.setEnableAppHangTracking(false)
            it.setBeforeSend(beforeSend)
        }
    }

    @Test
    fun `native exception conversion retains cause chain stack frames and debug images`() {
        start()
        val cause = IllegalArgumentException("inner")
        val throwable = IllegalStateException("outer", cause)
        val event = throwable.asSentryEvent(isHandled = true, markThreadAsCrashed = false)
        val exceptions = assertNotNull(event.exceptions).map { it as SentryException }
        assertEquals(2, exceptions.size)
        assertEquals("inner", exceptions.first().value)
        assertEquals("outer", exceptions.last().value)
        exceptions.forEach {
            assertEquals(true, it.mechanism?.handled?.boolValue)
            assertTrue(assertNotNull(it.stacktrace).frames.isNotEmpty())
            if (Platform.osFamily == OsFamily.WATCHOS) assertNull(it.threadId) else assertNotNull(it.threadId)
        }
        assertTrue(assertNotNull(event.debugMeta).isNotEmpty())
        assertCurrentThread(event, crashed = false)
    }

    @Test
    fun `fatal exception frames retain all referenced cached debug images`() {
        start()
        val event = IllegalStateException("outer", IllegalArgumentException("inner")).asSentryEvent()
        val exceptions = assertNotNull(event.exceptions).map { it as SentryException }
        val addresses =
            exceptions
                .flatMap { assertNotNull(it.stacktrace).frames }
                .mapNotNull { (it as SentryFrame).imageAddress }
                .toSet()
        assertTrue(addresses.isNotEmpty())
        // Some system frames (for example dyld) are absent from Cocoa's binary-image cache.
        // Every referenced image that the provider can resolve must be retained.
        val cachedAddresses =
            SentryDependencyContainer
                .sharedInstance()
                .debugImageProvider()
                .getDebugImagesFromCache()
                .map { (it as SentryDebugMeta).imageAddress }
                .toSet()
        val expectedAddresses = addresses.intersect(cachedAddresses)
        assertTrue(expectedAddresses.isNotEmpty())
        val imageAddresses = assertNotNull(event.debugMeta).map { (it as SentryDebugMeta).imageAddress }.toSet()
        assertTrue(
            imageAddresses.containsAll(expectedAddresses),
            "Missing exception images: ${expectedAddresses - imageAddresses}",
        )
        assertCurrentThread(event, crashed = true)
    }

    private fun assertCurrentThread(
        event: SentryEvent,
        crashed: Boolean,
    ) {
        val threads = assertNotNull(event.threads).map { it as SentryThread }
        // Cocoa's SENTRY_HAS_THREADS_API is false on watchOS. Exception frames still exist,
        // but enumerating native threads (and associating their IDs) is unavailable.
        if (Platform.osFamily == OsFamily.WATCHOS) {
            assertTrue(threads.isEmpty())
        } else {
            val currentThread = threads.first { it.current?.boolValue == true }
            assertEquals(crashed, currentThread.crashed?.boolValue == true)
            if (crashed) assertNull(currentThread.stacktrace)
        }
    }

    @Test
    fun `fatal envelope preparation honors dropped events`() {
        var called = false
        start {
            called = true
            null
        }
        assertNull(IllegalStateException("dropped").asSentryEnvelope())
        assertTrue(called)
    }

    @Test
    fun `fatal envelope preparation retains scope enrichment`() {
        var prepared: SentryEvent? = null
        start {
            prepared = it
            it
        }
        Sentry.configureScope { it.setTag("migration", "cocoa-v9") }
        val envelope = IllegalStateException("fatal").asSentryEnvelope()
        assertNotNull(envelope)
        assertEquals("cocoa-v9", assertNotNull(prepared).tags?.get("migration"))
        assertEquals(false, (prepared?.exceptions?.first() as SentryException).mechanism?.handled?.boolValue)
    }

    @Test
    fun `before send replacement cannot revive a duplicate Kotlin termination report`() {
        val options = CocoaSentryOptions()
        var called = false
        options.setBeforeSend {
            called = true
            SentryEvent()
        }
        options.prepareForInit()
        val duplicate = SentryEvent()
        (duplicate as Internal.Sentry.SentryEvent).isFatalEvent = true
        duplicate.tags = mapOf(KOTLIN_CRASH_TAG to KOTLIN_CRASH_TAG)

        val callback = assertNotNull(options.beforeSend())
        assertNull(callback(duplicate))
        assertFalse(called)
    }

    @Test
    fun `before send can replace the native event`() {
        val options = CocoaSentryOptions()
        val replacement = SentryEvent()
        options.setBeforeSend { replacement }
        options.prepareForInit()
        assertEquals(replacement.eventId, options.beforeSend()?.invoke(SentryEvent())?.eventId)
    }

    @Test
    fun `cpp monitor opt out survives close and default restart restores it`() {
        start()
        val enabledMask = sentrycrashcm_getActiveMonitors()
        assertTrue(enabledMask and SentryCrashMonitorTypeCPPException != 0u)
        Sentry.close()
        Sentry.initWithPlatformOptions {
            it.setDsn("http://public@127.0.0.1:9/1")
            it.setEnableAutoSessionTracking(false)
            it.setBeforeSend { null }
            setEnableUnhandledCppExceptionMonitoring(false)
        }
        val disabledMask = sentrycrashcm_getActiveMonitors()
        assertEquals(0u, disabledMask and SentryCrashMonitorTypeCPPException)
        assertEquals(enabledMask and SentryCrashMonitorTypeCPPException.inv(), disabledMask)
        Sentry.close()
        start()
        assertEquals(enabledMask, sentrycrashcm_getActiveMonitors())
    }
}
