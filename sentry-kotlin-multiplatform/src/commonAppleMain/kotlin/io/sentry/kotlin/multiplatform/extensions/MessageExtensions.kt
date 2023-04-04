package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.CocoaMessage
import io.sentry.kotlin.multiplatform.protocol.Message

internal fun CocoaMessage.toKmpMessage() = Message(
    message = message,
    params = params as? List<String>,
    formatted = formatted
)

internal fun Message.toCocoaMessage(): CocoaMessage {
    val scope = this@toCocoaMessage
    return (scope.formatted?.let { CocoaMessage(it) } ?: CocoaMessage()).apply {
        message = scope.message
        params = scope.params
    }
}
