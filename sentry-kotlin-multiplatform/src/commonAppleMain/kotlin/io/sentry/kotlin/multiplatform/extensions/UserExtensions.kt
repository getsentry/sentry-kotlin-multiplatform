package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.CocoaUser
import io.sentry.kotlin.multiplatform.protocol.User

internal fun User.toCocoaUser() = CocoaUser().apply {
    val scope = this@toCocoaUser
    this.userId = scope.id
    this.username = scope.username
    this.email = scope.email
    this.ipAddress = scope.ipAddress
}

internal fun CocoaUser.toKmpUser() = User().apply {
    val scope = this@toKmpUser
    this.id = scope.userId
    this.username = scope.username
    this.email = scope.email
    this.ipAddress = scope.ipAddress
}
