package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.AndroidUser
import io.sentry.kotlin.multiplatform.protocol.ISentryUser
import io.sentry.kotlin.multiplatform.protocol.User

fun ISentryUser.toAndroidUser(): AndroidUser {
    val androidUser = AndroidUser()
    androidUser.id = this.id
    androidUser.username = this.username
    androidUser.email = this.email
    androidUser.ipAddress = this.ipAddress
    androidUser.others = this.other.toMutableMap()
    androidUser.unknown = this.unknown.toMutableMap()
    return androidUser
}

fun AndroidUser.toKmpUser(): User {
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
