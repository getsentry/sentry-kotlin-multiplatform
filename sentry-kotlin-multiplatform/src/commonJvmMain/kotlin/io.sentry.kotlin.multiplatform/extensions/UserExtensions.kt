package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmUser
import io.sentry.kotlin.multiplatform.protocol.ISentryUser
import io.sentry.kotlin.multiplatform.protocol.User

fun ISentryUser.toJvmUser(): JvmUser {
    val androidUser = JvmUser()
    androidUser.id = this.id
    androidUser.username = this.username
    androidUser.email = this.email
    androidUser.ipAddress = this.ipAddress
    androidUser.others = this.other.toMutableMap()
    androidUser.unknown = this.unknown.toMutableMap()
    return androidUser
}

fun JvmUser.toKmpUser(): User {
    val kmpUser = User()
    kmpUser.id = this.id.toString()
    kmpUser.username = this.username.toString()
    kmpUser.email = this.email.toString()
    kmpUser.ipAddress = this.ipAddress.toString()
    this.others?.toMutableMap()?.let {
        kmpUser.other = it
    }
    this.unknown?.toMutableMap()?.let {
        kmpUser.unknown = it
    }
    return kmpUser
}
