package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.utils.fakeDsn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SentryOptionsTest : BaseSentryTest() {
    @Test
    fun `GIVEN sample rate WHEN set in Sentry init THEN does not crash`() {
        // GIVEN
        val sampleRate = 0.5

        // WHEN
        sentryInit {
            it.dsn = fakeDsn
            it.sampleRate = sampleRate
        }

        // THEN
        // does not crash
    }

    @Test
    fun `GIVEN traces sample rate WHEN set in Sentry init THEN does not crash`() {
        // GIVEN
        val traceSampleRate = 0.5

        // WHEN
        sentryInit {
            it.dsn = fakeDsn
            it.tracesSampleRate = traceSampleRate
        }

        // THEN
        // does not crash
    }

    @Test
    fun `Breadcrumb can be modified via callback in init and should return the modified Breadcrumb`() {
        val options = SentryOptions()

        fun mockInit(configuration: (SentryOptions) -> Unit) {
            configuration.invoke(options)
        }

        val expectedBreadcrumb = Breadcrumb().apply {
            message = "changed message"
            type = "changed type"
            category = "changed category"
            setData(mutableMapOf("data1" to 12, "data2" to "value", "key" to "value"))
        }

        val breadcrumb = Breadcrumb().apply {
            message = "another message"
            type = "another type"
            category = "another category"
            setData(mutableMapOf("data1" to 12, "data2" to "value"))
        }

        mockInit {
            it.beforeBreadcrumb = { breadcrumb ->
                breadcrumb.apply {
                    setData("key", "value")
                    message = "changed message"
                    type = "changed type"
                    category = "changed category"
                }
            }
        }

        val modifiedBreadcrumb = breadcrumb.let(options.beforeBreadcrumb!!)

        assertEquals(expectedBreadcrumb, modifiedBreadcrumb)
    }

    @Test
    fun `Breadcrumb can be dropped via beforeBreadcrumb hook`() {
        val options = SentryOptions()

        fun mockInit(configuration: (SentryOptions) -> Unit) {
            configuration.invoke(options)
        }

        mockInit {
            it.beforeBreadcrumb = { breadcrumb ->
                breadcrumb.message = "message"
                null
            }
        }

        val modifiedBreadcrumb = options.beforeBreadcrumb?.invoke(Breadcrumb())

        assertEquals(null, modifiedBreadcrumb)
    }

    @Test
    fun `GIVEN SentryOptions THEN default values are set`() {
        val options = SentryOptions()

        assertNull(options.dsn)
        assertTrue(options.attachStackTrace)
        assertTrue(options.attachThreads)
        assertNull(options.release)
        assertFalse(options.debug)
        assertNull(options.environment)
        assertNull(options.dist)
        assertTrue(options.enableAutoSessionTracking)
        assertEquals(DEFAULT_SESSION_INTERVAL_MILLIS, options.sessionTrackingIntervalMillis)
        assertFalse(options.attachScreenshot)
        assertNull(options.beforeBreadcrumb)
        assertNull(options.beforeSend)
        assertNull(options.sdk)
        assertEquals(DEFAULT_MAX_BREADCRUMBS, options.maxBreadcrumbs)
        assertEquals(DEFAULT_MAX_ATTACHMENT_SIZE, options.maxAttachmentSize)
        assertFalse(options.attachViewHierarchy)
        assertTrue(options.enableCaptureFailedRequests)
        assertEquals(listOf(HttpStatusCodeRange()), options.failedRequestStatusCodes)
        assertEquals(listOf(".*"), options.failedRequestTargets)
        assertNull(options.sampleRate)
        assertNull(options.tracesSampleRate)
        assertTrue(options.enableAppHangTracking)
        assertEquals(2000L, options.appHangTimeoutIntervalMillis)
        assertTrue(options.isAnrEnabled)
        assertEquals(5000L, options.anrTimeoutIntervalMillis)
        assertNull(options.experimental.sessionReplay.onErrorSampleRate)
        assertNull(options.experimental.sessionReplay.sessionSampleRate)
        assertTrue(options.experimental.sessionReplay.maskAllText)
        assertTrue(options.experimental.sessionReplay.maskAllImages)
        assertEquals(SentryReplayOptions.Quality.MEDIUM, options.experimental.sessionReplay.quality)
        assertTrue(options.enableWatchdogTerminationTracking)
    }

    @Test
    fun `GIVEN non-default SentryOptions WHEN options initialized THEN applies values to native options`() {
        val options = SentryOptions().apply {
            dsn = fakeDsn
            attachStackTrace = false
            release = "release"
            debug = true
            environment = "environment"
            dist = "dist"
            enableAutoSessionTracking = false
            sessionTrackingIntervalMillis = 1000L
            maxBreadcrumbs = 10
            maxAttachmentSize = 100L
            sampleRate = 0.5
            tracesSampleRate = 0.5
            attachScreenshot = true
            attachViewHierarchy = true
            enableAppHangTracking = false
            appHangTimeoutIntervalMillis = 1000L
            isAnrEnabled = false
            anrTimeoutIntervalMillis = 1000L
            enableWatchdogTerminationTracking = false
            experimental.sessionReplay.onErrorSampleRate = 0.5
            experimental.sessionReplay.sessionSampleRate = 0.5
            experimental.sessionReplay.maskAllText = false
            experimental.sessionReplay.maskAllImages = false
            experimental.sessionReplay.quality = SentryReplayOptions.Quality.LOW
        }

        val platformOptions = createPlatformOptions()
        platformOptions.applyFromOptions(options)

        assertEquals(fakeDsn, platformOptions.dsn)
        assertFalse(platformOptions.attachStackTrace)
        assertEquals("release", platformOptions.release)
        assertTrue(platformOptions.debug)
        assertEquals("environment", platformOptions.environment)
        assertEquals("dist", platformOptions.dist)
        assertFalse(platformOptions.enableAutoSessionTracking)
        assertEquals(1000L, platformOptions.sessionTrackingIntervalMillis)
        assertEquals(10, platformOptions.maxBreadcrumbs)
        assertEquals(100L, platformOptions.maxAttachmentSize)
        assertEquals(0.5, platformOptions.sampleRate)
        assertEquals(0.5, platformOptions.tracesSampleRate)

        platformOptions.assertPlatformSpecificOptions(options)
    }
}

expect fun PlatformOptions.assertPlatformSpecificOptions(kmpOptions: SentryOptions)
