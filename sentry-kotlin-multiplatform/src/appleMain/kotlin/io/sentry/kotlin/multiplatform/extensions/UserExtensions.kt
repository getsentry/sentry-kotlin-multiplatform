package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.CocoaUser
import io.sentry.kotlin.multiplatform.protocol.User

internal fun User.toCocoaUser(): CocoaUser {
    val ref = this
    return CocoaUser().apply {
        userId = ref.id
        username = ref.username
        email = ref.email
        ipAddress = ref.ipAddress
    }
}
