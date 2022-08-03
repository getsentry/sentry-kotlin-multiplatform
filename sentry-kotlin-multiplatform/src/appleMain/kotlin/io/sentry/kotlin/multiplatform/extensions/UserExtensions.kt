package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.CocoaUser
import io.sentry.kotlin.multiplatform.protocol.User

fun User.toCocoaUser(): CocoaUser {
    val cocoaUser = CocoaUser()
    cocoaUser.userId = this.id
    cocoaUser.username = this.username
    cocoaUser.email = this.email
    cocoaUser.ipAddress = this.ipAddress
    return cocoaUser
}