package io.sentry.kotlin.multiplatform

public data class CustomSamplingContext(
    public val data: Map<String, Any?> = mapOf()
)