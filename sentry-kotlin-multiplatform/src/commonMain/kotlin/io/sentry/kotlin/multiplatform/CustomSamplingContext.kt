package io.sentry.kotlin.multiplatform

public data class CustomSamplingContext(
    private val data: MutableMap<String, Any?> = mutableMapOf()
)