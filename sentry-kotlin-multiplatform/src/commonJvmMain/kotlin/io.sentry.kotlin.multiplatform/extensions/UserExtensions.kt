package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmUser
import io.sentry.kotlin.multiplatform.protocol.ISentryUser
import io.sentry.kotlin.multiplatform.protocol.User

internal fun ISentryUser.toJvmUser(): JvmUser {
    val ref = this
    return JvmUser().apply {
        id = ref.id
        username = ref.username
        email = ref.email
        ipAddress = ref.ipAddress
        others = ref.other?.toMutableMap()
        unknown = ref.unknown?.toMutableMap()
    }
}

internal fun JvmUser.toKmpUser(): User {
    val ref = this
    return User().apply {
        id = ref.id.toString()
        username = ref.username.toString()
        email = ref.email.toString()
        ipAddress = ref.ipAddress.toString()
        ref.others?.toMutableMap()?.let {
            other = it
        }
        ref.unknown?.toMutableMap()?.let {
            unknown = it
        }
    }
}
