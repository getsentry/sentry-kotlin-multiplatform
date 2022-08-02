package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.CocoaSentryUser
import io.sentry.kotlin.multiplatform.protocol.SentryUser

fun SentryUser.toCocoaSentryUser(): CocoaSentryUser {
    val cocoaUser = CocoaSentryUser()
    cocoaUser.userId = this.id
    cocoaUser.username = this.username
    cocoaUser.email = this.email
    cocoaUser.ipAddress = this.ipAddress
    return cocoaUser
}
