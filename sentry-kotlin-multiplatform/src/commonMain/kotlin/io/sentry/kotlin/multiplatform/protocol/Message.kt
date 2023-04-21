package io.sentry.kotlin.multiplatform.protocol

public data class Message(
    public var message: String? = null,
    public var params: List<String>? = null,
    public var formatted: String? = null
)
