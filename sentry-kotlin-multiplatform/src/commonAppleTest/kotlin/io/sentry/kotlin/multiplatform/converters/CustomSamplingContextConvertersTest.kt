package io.sentry.kotlin.multiplatform.converters

import io.sentry.kotlin.multiplatform.CocoaCustomSamplingContext
import io.sentry.kotlin.multiplatform.CustomSamplingContext
import io.sentry.kotlin.multiplatform.fakes.createFakeCustomSamplingContext
import kotlin.test.Test
import kotlin.test.assertTrue

class CustomSamplingContextConvertersTest {
    @Test
    fun `GIVEN customSamplingContext WHEN toCocoa called THEN value is CocoaCustomSamplingContext`() {
        // GIVEN
        val customSamplingContext: CustomSamplingContext = createFakeCustomSamplingContext()

        // WHEN
        val cocoaCustomSamplingContext = customSamplingContext.toCocoa()

        // THEN
        assertTrue(cocoaCustomSamplingContext is CocoaCustomSamplingContext)
    }
}
