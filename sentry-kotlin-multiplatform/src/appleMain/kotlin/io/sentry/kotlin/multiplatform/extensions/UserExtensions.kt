package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.CocoaUser
import io.sentry.kotlin.multiplatform.protocol.User

internal fun User.toCocoaUser(): CocoaUser {
    val outerScope = this
    return CocoaUser().apply {
        userId = outerScope.id
        username = outerScope.username
        email = outerScope.email
        ipAddress = outerScope.ipAddress
    }
}
