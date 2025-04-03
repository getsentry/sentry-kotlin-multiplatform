package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmContexts

internal fun JvmContexts.toMap(): Map<String, Any> {
    val resultMap = mutableMapOf<String, Any>()
    for (key in keys()) {
        val value = get(key)
        value?.let {
            resultMap[key] = it
        }
    }
    return resultMap
}
