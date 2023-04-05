package io.sentry.kotlin.multiplatform.protocol

public data class SentryException(
    val type: String? = null,
    val value: String? = null,
    val module: String? = null,
    val threadId: Long? = null
)
