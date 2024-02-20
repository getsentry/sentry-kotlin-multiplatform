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

        assertNull(options.dsn, "DSN should be null by default.")
        assertTrue(options.attachStackTrace, "attachStackTrace should be true by default.")
        assertTrue(options.attachThreads, "attachThreads should be true by default.")
        assertNull(options.release, "Release should be null by default.")
        assertFalse(options.debug, "Debug should be false by default.")
        assertNull(options.environment, "Environment should be null by default.")
        assertNull(options.dist, "Dist should be null by default.")
        assertTrue(options.enableAutoSessionTracking, "enableAutoSessionTracking should be true by default.")
        assertEquals(DEFAULT_SESSION_INTERVAL_MILLIS, options.sessionTrackingIntervalMillis, "sessionTrackingIntervalMillis should match the default value.")
        assertFalse(options.attachScreenshot, "attachScreenshot should be false by default.")
        assertNull(options.beforeBreadcrumb, "beforeBreadcrumb should be null by default.")
        assertNull(options.beforeSend, "beforeSend should be null by default.")
        assertNull(options.sdk, "SDK version should be null by default.")
        assertEquals(DEFAULT_MAX_BREADCRUMBS, options.maxBreadcrumbs, "maxBreadcrumbs should match the default value.")
        assertEquals(DEFAULT_MAX_ATTACHMENT_SIZE, options.maxAttachmentSize, "maxAttachmentSize should match the default value.")
        assertFalse(options.attachViewHierarchy, "attachViewHierarchy should be false by default.")
        assertTrue(options.enableCaptureFailedRequests, "enableCaptureFailedRequests should be true by default.")
        assertEquals(listOf(HttpStatusCodeRange()), options.failedRequestStatusCodes, "failedRequestStatusCodes should contain the default range.")
        assertEquals(listOf(".*"), options.failedRequestTargets, "failedRequestTargets should contain the default regex for all targets.")
        assertNull(options.sampleRate, "sampleRate should be null by default.")
        assertNull(options.tracesSampleRate, "tracesSampleRate should be null by default.")
        assertTrue(options.enableAppHangTracking, "enableAppHangTracking should be true by default.")
        assertEquals(2000L, options.appHangTimeoutIntervalMillis, "appHangTimeoutIntervalMillis should be 2000 milliseconds by default.")
        assertTrue(options.isAnrEnabled, "anrEnabled should be true by default.")
        assertEquals(5000L, options.anrTimeoutIntervalMillis, "anrTimeoutIntervalMillis should be 5000 milliseconds by default.")
    }
}
