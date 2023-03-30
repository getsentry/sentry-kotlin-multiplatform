package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.CocoaUser
import io.sentry.kotlin.multiplatform.protocol.User

internal fun User.toCocoaUser() = CocoaUser().apply {
    val scope = this@toCocoaUser
    userId = scope.id
    username = scope.username
    email = scope.email
    ipAddress = scope.ipAddress
}

internal fun CocoaUser.toKmpUser() = User().apply {
    val scope = this@toKmpUser
    id = scope.userId.toString()
    username = scope.username.toString()
    email = scope.email.toString()
    ipAddress = scope.ipAddress.toString()
    setData(scope.data)
}
