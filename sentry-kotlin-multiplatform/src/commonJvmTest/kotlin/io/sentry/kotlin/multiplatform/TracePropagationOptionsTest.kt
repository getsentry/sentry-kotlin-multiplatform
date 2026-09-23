package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toJvmSentryOptionsCallback
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class TracePropagationOptionsTest {
    @Test
    fun `shared options forward strict continuation and organization ID`() {
        for (strict in listOf(false, true)) {
            for (organizationId in listOf(null, "123456")) {
                val options =
                    SentryOptions().apply {
                        strictTraceContinuation = strict
                        orgId = organizationId
                    }
                val nativeOptions = JvmSentryOptions()
                options.toJvmSentryOptionsCallback().invoke(nativeOptions)

                assertEquals(strict, nativeOptions.isStrictTraceContinuation)
                assertEquals(organizationId, nativeOptions.orgId)
            }
        }
    }

    @Test
    fun `shared options forward traceparent propagation`() {
        for (enabled in listOf(false, true)) {
            val nativeOptions = JvmSentryOptions()
            SentryOptions()
                .apply { enablePropagateTraceparent = enabled }
                .toJvmSentryOptionsCallback()
                .invoke(nativeOptions)

            assertEquals(enabled, nativeOptions.isPropagateTraceparent)
        }
    }

    @Test
    fun `shared defaults reset previously configured native options`() {
        val nativeOptions = JvmSentryOptions()
        SentryOptions()
            .apply {
                enablePropagateTraceparent = true
                strictTraceContinuation = true
                orgId = "123456"
            }.toJvmSentryOptionsCallback()
            .invoke(nativeOptions)

        SentryOptions().toJvmSentryOptionsCallback().invoke(nativeOptions)

        assertFalse(nativeOptions.isStrictTraceContinuation)
        assertFalse(nativeOptions.isPropagateTraceparent)
        assertNull(nativeOptions.orgId)
    }
}
