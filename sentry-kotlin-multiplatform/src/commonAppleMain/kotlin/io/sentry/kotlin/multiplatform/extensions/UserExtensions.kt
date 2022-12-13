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

internal fun CocoaUser.toKmpUser(): User {
    val outerScope = this
    return User().apply {
        id = outerScope.userId.toString()
        username = outerScope.username.toString()
        email = outerScope.email.toString()
        ipAddress = outerScope.ipAddress.toString()
        setData(outerScope.data)
    }
}
