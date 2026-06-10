package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmMessage
import io.sentry.kotlin.multiplatform.protocol.Message

internal fun JvmMessage.toKmpMessage() = Message(
    message = message,
    params = params,
    formatted = formatted
)

internal fun Message.toJvmMessage() = JvmMessage().apply {
    val scope = this@toJvmMessage
    message = scope.message
    params = scope.params
    formatted = scope.formatted
}
