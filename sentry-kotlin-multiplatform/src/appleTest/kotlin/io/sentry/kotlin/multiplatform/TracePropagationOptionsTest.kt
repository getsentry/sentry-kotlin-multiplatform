package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toCocoaOptionsConfiguration
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
                val nativeOptions = CocoaSentryOptions()
                options.toCocoaOptionsConfiguration().invoke(nativeOptions)

                assertEquals(strict, nativeOptions.strictTraceContinuation())
                assertEquals(organizationId, nativeOptions.orgId())
            }
        }
    }

    @Test
    fun `shared options forward traceparent propagation`() {
        for (enabled in listOf(false, true)) {
            val nativeOptions = CocoaSentryOptions()
            SentryOptions()
                .apply { enablePropagateTraceparent = enabled }
                .toCocoaOptionsConfiguration()
                .invoke(nativeOptions)

            assertEquals(enabled, nativeOptions.enablePropagateTraceparent())
        }
    }

    @Test
    fun `shared defaults reset previously configured native options`() {
        val nativeOptions = CocoaSentryOptions()
        SentryOptions()
            .apply {
                enablePropagateTraceparent = true
                strictTraceContinuation = true
                orgId = "123456"
            }.toCocoaOptionsConfiguration()
            .invoke(nativeOptions)

        SentryOptions().toCocoaOptionsConfiguration().invoke(nativeOptions)

        assertFalse(nativeOptions.strictTraceContinuation())
        assertFalse(nativeOptions.enablePropagateTraceparent())
        assertNull(nativeOptions.orgId())
    }
}
