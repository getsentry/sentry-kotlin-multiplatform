package io.sentry.kotlin.multiplatform.converters

import io.sentry.kotlin.multiplatform.CocoaCustomSamplingContext

internal fun Map<String, Any>.toCocoa(): CocoaCustomSamplingContext {
    return this as CocoaCustomSamplingContext
}
