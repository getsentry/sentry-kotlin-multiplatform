package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.CocoaUser
import io.sentry.kotlin.multiplatform.protocol.User
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
internal fun User.toCocoaUser() = CocoaUser().apply {
    val scope = this@toCocoaUser
    userId = scope.id
    username = scope.username
    email = scope.email
    ipAddress = scope.ipAddress
}

@OptIn(ExperimentalForeignApi::class)
internal fun CocoaUser.toKmpUser() = User().apply {
    val scope = this@toKmpUser
    id = scope.userId
    username = scope.username
    email = scope.email
    ipAddress = scope.ipAddress
}
