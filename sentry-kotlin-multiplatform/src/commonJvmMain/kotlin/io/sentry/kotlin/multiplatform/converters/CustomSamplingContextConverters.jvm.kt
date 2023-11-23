package io.sentry.kotlin.multiplatform.converters

import io.sentry.kotlin.multiplatform.JvmCustomSamplingContext

internal fun Map<String, Any>.toJvm(): JvmCustomSamplingContext {
    val jvmCustomSamplingContext = JvmCustomSamplingContext()
    forEach { (key, value) ->
        jvmCustomSamplingContext[key] = value
    }
    return jvmCustomSamplingContext
}
