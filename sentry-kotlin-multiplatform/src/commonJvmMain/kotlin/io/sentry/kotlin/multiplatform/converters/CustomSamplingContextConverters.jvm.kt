package io.sentry.kotlin.multiplatform.converters

import io.sentry.kotlin.multiplatform.CustomSamplingContext
import io.sentry.kotlin.multiplatform.JvmCustomSamplingContext

internal fun CustomSamplingContext.toJvm(): JvmCustomSamplingContext {
    val jvmCustomSamplingContext = JvmCustomSamplingContext()
    this?.forEach { (key, value) ->
        jvmCustomSamplingContext[key] = value
    }
    return jvmCustomSamplingContext
}
