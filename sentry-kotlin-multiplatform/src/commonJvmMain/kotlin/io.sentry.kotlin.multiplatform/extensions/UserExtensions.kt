package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmUser
import io.sentry.kotlin.multiplatform.protocol.ISentryUser
import io.sentry.kotlin.multiplatform.protocol.User

internal fun ISentryUser.toJvmUser(): JvmUser {
    val outerScope = this
    return JvmUser().apply {
        id = outerScope.id
        username = outerScope.username
        email = outerScope.email
        ipAddress = outerScope.ipAddress
        others = outerScope.other?.toMutableMap()
        unknown = outerScope.unknown?.toMutableMap()
    }
}

internal fun JvmUser.toKmpUser(): User {
    val outerScope = this
    return User().apply {
        id = outerScope.id.toString()
        username = outerScope.username.toString()
        email = outerScope.email.toString()
        ipAddress = outerScope.ipAddress.toString()
        other = outerScope.others?.toMutableMap()?.let { it }
        unknown = outerScope.unknown?.toMutableMap()?.let { it }
    }
}
