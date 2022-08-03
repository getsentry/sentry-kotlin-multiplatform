package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.AndroidUser
import io.sentry.kotlin.multiplatform.protocol.User
import io.sentry.util.CollectionUtils

fun User.toAndroidUser(): AndroidUser {
    val androidUser = AndroidUser()
    androidUser.id = this.id
    androidUser.username = this.username
    androidUser.email = this.email
    androidUser.ipAddress = this.ipAddress
    androidUser.others = CollectionUtils.newConcurrentHashMap(this.other)
    androidUser.unknown = CollectionUtils.newConcurrentHashMap(this.unknown)
    return androidUser
}

fun AndroidUser.toKmpUser(): User {
    val kmpUser = User()
    kmpUser.id = this.id.toString()
    kmpUser.username = this.username.toString()
    kmpUser.email = this.email.toString()
    kmpUser.ipAddress = this.ipAddress.toString()
    CollectionUtils.newConcurrentHashMap(this.others)?.let {
        kmpUser.other = it
    }
    CollectionUtils.newConcurrentHashMap(this.unknown)?.let {
        kmpUser.unknown = it
    }
    return kmpUser
}
