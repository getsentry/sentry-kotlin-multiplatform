package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.AndroidSentryUser
import io.sentry.kotlin.multiplatform.SentryUser
import io.sentry.util.CollectionUtils

fun SentryUser.toAndroidSentryUser(): AndroidSentryUser {
    val androidUser = AndroidSentryUser()
    androidUser.id = this.id
    androidUser.username = this.username
    androidUser.email = this.email
    androidUser.ipAddress = this.ipAddress
    androidUser.others = CollectionUtils.newConcurrentHashMap(this.other)
    androidUser.unknown = CollectionUtils.newConcurrentHashMap(this.unknown)
    return androidUser
}

fun AndroidSentryUser.toKMPSentryUser(): SentryUser {
    val kmpSentryUser = SentryUser()
    kmpSentryUser.id = this.id.toString()
    kmpSentryUser.username = this.username.toString()
    kmpSentryUser.email = this.email.toString()
    kmpSentryUser.ipAddress = this.ipAddress.toString()
    kmpSentryUser.other = CollectionUtils.newConcurrentHashMap(this.others) as Map<String, String>
    kmpSentryUser.unknown = CollectionUtils.newConcurrentHashMap(this.unknown) as Map<String, Any>
    return kmpSentryUser
}
