package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.CocoaMessage
import io.sentry.kotlin.multiplatform.protocol.Message

internal fun CocoaMessage.toKmpMessage() = Message(
    message = this.message,
    params = this.params as? List<String>,
    formatted = this.formatted
)

internal fun Message.toCocoaMessage(): CocoaMessage {
    val scope = this@toCocoaMessage
    val cocoaMessage = scope.formatted?.let { CocoaMessage(it) } ?: CocoaMessage()
    return cocoaMessage.apply {
        this.message = scope.message
        this.params = scope.params
    }
}
