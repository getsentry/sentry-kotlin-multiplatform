package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmUser
import io.sentry.kotlin.multiplatform.protocol.User

internal fun User.toJvmUser() = JvmUser().apply {
    val scope = this@toJvmUser
    this.id = scope.id
    this.username = scope.username
    this.email = scope.email
    this.ipAddress = scope.ipAddress
    this.others = scope.other?.toMutableMap()
    this.unknown = scope.unknown?.toMutableMap()
}

internal fun JvmUser.toKmpUser() = User().apply {
    val scope = this@toKmpUser
    this.id = scope.id
    this.username = scope.username
    this.email = scope.email
    this.ipAddress = scope.ipAddress
    this.other = scope.others?.toMutableMap()
    this.unknown = scope.unknown?.toMutableMap()
}
