package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.AndroidUser
import io.sentry.kotlin.multiplatform.protocol.SentryUser
import io.sentry.util.CollectionUtils

fun SentryUser.toAndroidSentryUser(): AndroidUser {
    val androidUser = AndroidUser()
    androidUser.id = this.id
    androidUser.username = this.username
    androidUser.email = this.email
    androidUser.ipAddress = this.ipAddress
    androidUser.others = CollectionUtils.newConcurrentHashMap(this.other)
    androidUser.unknown = CollectionUtils.newConcurrentHashMap(this.unknown)
    return androidUser
}

fun AndroidUser.toKmpSentryUser(): SentryUser {
    val kmpSentryUser = SentryUser()
    kmpSentryUser.id = this.id.toString()
    kmpSentryUser.username = this.username.toString()
    kmpSentryUser.email = this.email.toString()
    kmpSentryUser.ipAddress = this.ipAddress.toString()
    CollectionUtils.newConcurrentHashMap(this.others)?.let {
        kmpSentryUser.other = it
    }
    CollectionUtils.newConcurrentHashMap(this.unknown)?.let {
        kmpSentryUser.unknown = it
    }
    return kmpSentryUser
}
