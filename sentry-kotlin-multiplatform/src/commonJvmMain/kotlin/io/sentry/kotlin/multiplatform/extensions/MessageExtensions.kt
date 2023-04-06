package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmMessage
import io.sentry.kotlin.multiplatform.protocol.Message

internal fun JvmMessage.toKmpMessage() = Message(
    message = this.message,
    params = this.params,
    formatted = this.formatted
)

internal fun Message.toJvmMessage() = JvmMessage().apply {
    val scope = this@toJvmMessage
    this.message = scope.message
    this.params = scope.params
    this.formatted = scope.formatted
}
