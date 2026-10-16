package io.sentry.kotlin.multiplatform

import Internal.Sentry.SentryCrashMonitorTypeCPPException
import Internal.Sentry.SentryCrashMonitorTypeNSException
import Internal.Sentry.SentrySDKInternal
import Internal.Sentry.sentrycrash_setMonitoring
import Internal.Sentry.sentrycrashcm_getActiveMonitors
import cocoapods.Sentry.SentryDebugMeta
import cocoapods.Sentry.SentryDependencyContainer
import cocoapods.Sentry.SentryEvent
import cocoapods.Sentry.SentryException
import cocoapods.Sentry.SentryFileContents
import cocoapods.Sentry.SentryFrame
import cocoapods.Sentry.SentryThread
import io.sentry.kotlin.multiplatform.nsexception.KOTLIN_CRASH_TAG
import io.sentry.kotlin.multiplatform.nsexception.ThrowableNSException
import io.sentry.kotlin.multiplatform.nsexception.asNSException
import io.sentry.kotlin.multiplatform.nsexception.asSentryEnvelope
import io.sentry.kotlin.multiplatform.nsexception.asSentryEvent
import io.sentry.kotlin.multiplatform.nsexception.asSentryStacktrace
import io.sentry.kotlin.multiplatform.nsexception.captureUnhandledException
import io.sentry.kotlin.multiplatform.nsexception.debugImagesForFrames
import platform.Foundation.NSException
import platform.Foundation.NSNumber
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSUUID
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
    @AfterTest
    fun close() {
        Sentry.close()
    }

    private fun start(
        sampleRate: Double = 1.0,
        beforeSend: (SentryEvent?) -> SentryEvent? = { null },
    ) {
        Sentry.initWithPlatformOptions {
            it.setDsn("http://public@127.0.0.1:9/1")
            it.setEnableAutoSessionTracking(false)
            it.setEnableAppHangTracking(false)
            it.setSampleRate(NSNumber(sampleRate))
            it.setBeforeSend(beforeSend)
        }
    }

    @Test
    fun `manual logger forwards to Cocoa when enableLogs is false`() {
        var callbacks = 0
        Sentry.initWithPlatformOptions {
            it.setDsn("http://public@127.0.0.1:9/1")
            it.setEnableAutoSessionTracking(false)
            it.setEnableLogs(false)
            it.setBeforeSendLog { _ ->
                callbacks++
                null
            }
        }
        val logger = Sentry.logger
        logger.info("manual log")
        assertEquals(1, callbacks)
        Sentry.close()
        logger.info("after close")
        assertEquals(1, callbacks)
    }

    @Test
    fun `exception without stack frames has no native stacktrace`() {
        start()
        val exception = NSException(name = "EmptyStack", reason = "No captured frames", userInfo = null)
        assertNull(exception.asSentryStacktrace())
        val emptyStack = ThrowableNSException("EmptyStack", "No captured frames", emptyList())
        assertNull(emptyStack.asSentryStacktrace())
    }

    @Test
    fun `exception stacktrace uses native in app includes`() {
        start()
        val exception = IllegalStateException("in-app frame").asNSException()
        val originalFrames = assertNotNull(exception.asSentryStacktrace()).frames.map { it as SentryFrame }
        val image = assertNotNull(originalFrames.firstOrNull { it.`package` != null }?.`package`)
        Sentry.close()
        Sentry.initWithPlatformOptions {
            it.setDsn("http://public@127.0.0.1:9/1")
            it.setEnableAutoSessionTracking(false)
            it.addInAppInclude(image.substringAfterLast('/'))
        }
        val frames = assertNotNull(exception.asSentryStacktrace()).frames.map { it as SentryFrame }
        val includedFrames = frames.filter { it.`package` == image }
        assertTrue(includedFrames.isNotEmpty())
        assertTrue(includedFrames.all { it.inApp?.boolValue == true })
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

    @Test
    fun `hybrid debug images ignore invalid addresses and deduplicate frames`() {
        start()
        val image =
            assertNotNull(
                SentryDependencyContainer
                    .sharedInstance()
                    .debugImageProvider()
                    .getDebugImagesFromCache()
                    .firstOrNull(),
            ) as SentryDebugMeta
        val frame = SentryFrame().apply { imageAddress = image.imageAddress }
        val invalid = SentryFrame().apply { imageAddress = "not-an-address" }
        val images = debugImagesForFrames(listOf(frame, frame, invalid, SentryFrame()))
        assertEquals(1, images.size)
        val actual = images.single() as SentryDebugMeta
        assertEquals(image.imageAddress, actual.imageAddress)
        assertEquals(image.debugID, actual.debugID)
        assertEquals(image.codeFile, actual.codeFile)
        assertEquals(image.imageSize, actual.imageSize)
    }

    @Test
    fun `debug images normalize equivalent addresses and reject malformed hex`() {
        start()
        val image =
            SentryDependencyContainer
                .sharedInstance()
                .debugImageProvider()
                .getDebugImagesFromCache()
                .first() as SentryDebugMeta
        val address = assertNotNull(image.imageAddress).removePrefix("0x")
        val equivalent = listOf("0x$address", address, address.uppercase(), "+$address", "0x+$address", "0x00$address")
        val actual = debugImagesForFrames(equivalent.map { value -> SentryFrame().apply { imageAddress = value } })
        assertEquals(1, actual.size)
        assertEquals(image.imageAddress, actual.single().imageAddress)

        val invalid = listOf(null, "", "0x", "0X$address", "$address!", " $address", "$address ", "10000000000000000")
        assertTrue(debugImagesForFrames(invalid.map { value -> SentryFrame().apply { imageAddress = value } }).isEmpty())
    }

    @Test
    fun `debug images retain descending image order`() {
        start()
        val images =
            SentryDependencyContainer
                .sharedInstance()
                .debugImageProvider()
                .getDebugImagesFromCache()
                .map { it as SentryDebugMeta }
                .take(3)
        val frames = images.map { image -> SentryFrame().apply { imageAddress = image.imageAddress } }
        val expected = images.sortedByDescending { assertNotNull(it.imageAddress).removePrefix("0x").toULong(16) }
        assertEquals(expected.map { it.imageAddress }, debugImagesForFrames(frames).map { it.imageAddress })
        assertTrue(debugImagesForFrames(emptyList()).isEmpty())
    }

    @Test
    fun `inactive native exception monitor persists the enriched fatal event`() {
        var prepared: SentryEvent? = null
        Sentry.initWithPlatformOptions {
            it.setDsn("http://public@127.0.0.1:9/1")
            it.setCacheDirectoryPath(NSTemporaryDirectory() + NSUUID().UUIDString)
            it.setEnableAutoSessionTracking(false)
            it.setEnableAppHangTracking(false)
            it.setBeforeSend { event ->
                prepared = event
                event
            }
        }
        disableNativeExceptionMonitor()
        Sentry.configureScope { it.setTag("fallback", "scope") }
        val files = assertNotNull(SentryDependencyContainer.sharedInstance().fileManager())
        val before = files.getAllEnvelopes().map { (it as SentryFileContents).path() }.toSet()
        IllegalStateException("fallback crash").captureUnhandledException()
        val stored = files.getAllEnvelopes().map { it as SentryFileContents }.filter { it.path() !in before }
        assertEquals(1, stored.size)
        assertTrue(stored.single().contents().length > 0u)
        val event = assertNotNull(prepared)
        assertEquals("scope", event.tags?.get("fallback"))
        assertEquals(false, (event.exceptions?.single() as SentryException).mechanism?.handled?.boolValue)
        assertTrue((event as Internal.Sentry.SentryEvent).isFatalEvent)
        assertTerminationCrashTagged()
    }

    private fun disableNativeExceptionMonitor() {
        sentrycrash_setMonitoring(sentrycrashcm_getActiveMonitors() and SentryCrashMonitorTypeNSException.inv())
        assertEquals(0u, sentrycrashcm_getActiveMonitors() and SentryCrashMonitorTypeNSException)
    }

    private fun envelopePaths(): Set<String> =
        assertNotNull(SentryDependencyContainer.sharedInstance().fileManager())
            .getAllEnvelopes()
            .map { (it as SentryFileContents).path() }
            .toSet()

    private fun assertTerminationCrashTagged() {
        val tags = SentrySDKInternal.currentHub().scope.tagDictionary
        assertEquals(KOTLIN_CRASH_TAG, tags?.objectForKey(KOTLIN_CRASH_TAG))
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
    fun `fallback capture honors dropped events`() {
        var called = false
        start {
            called = true
            null
        }
        disableNativeExceptionMonitor()
        val before = envelopePaths()
        IllegalStateException("dropped").captureUnhandledException()
        assertTrue(called)
        assertEquals(before, envelopePaths())
        assertTerminationCrashTagged()
    }

    @Test
    fun `fallback capture honors sampling before beforeSend`() {
        var called = false
        start(sampleRate = 0.0) {
            called = true
            it
        }
        disableNativeExceptionMonitor()
        val before = envelopePaths()
        IllegalStateException("sampled out").captureUnhandledException()
        assertFalse(called)
        assertEquals(before, envelopePaths())
        assertTerminationCrashTagged()
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
