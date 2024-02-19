package io.sentry.kotlin.multiplatform.converters

import io.sentry.kotlin.multiplatform.CocoaCustomSamplingContext
import io.sentry.kotlin.multiplatform.CustomSamplingContext

internal fun CustomSamplingContext.toCocoa(): CocoaCustomSamplingContext {
    return this as CocoaCustomSamplingContext
}
