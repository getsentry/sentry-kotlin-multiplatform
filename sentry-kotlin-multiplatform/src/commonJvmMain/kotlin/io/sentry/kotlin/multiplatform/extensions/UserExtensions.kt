package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmUser
import io.sentry.kotlin.multiplatform.protocol.User

internal fun User.toJvmUser() = JvmUser().apply {
    val scope = this@toJvmUser
    id = scope.id
    username = scope.username
    email = scope.email
    ipAddress = scope.ipAddress
    others = scope.other?.toMutableMap()
    unknown = scope.unknown?.toMutableMap()
}

internal fun JvmUser.toKmpUser() = User().apply {
    val scope = this@toKmpUser
    id = scope.id
    username = scope.username
    email = scope.email
    ipAddress = scope.ipAddress
    other = scope.others?.toMutableMap()
    unknown = scope.unknown?.toMutableMap()
}
