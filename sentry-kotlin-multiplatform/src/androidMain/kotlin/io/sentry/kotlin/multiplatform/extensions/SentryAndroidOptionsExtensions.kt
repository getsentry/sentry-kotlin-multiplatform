package io.sentry.kotlin.multiplatform.extensions

import io.sentry.android.core.SentryAndroidOptions
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.sentry_kotlin_multiplatform.BuildConfig

internal fun SentryOptions.toAndroidSentryOptionsCallback(): (SentryAndroidOptions) -> Unit = {
    // Apply base options available to all JVM targets
    it.applyJvmBaseOptions(this)

    // Apply Android specific options
    it.isAttachScreenshot = this.attachScreenshot

    it.setBeforeSend { event, _ ->
        event.sdk?.addPackage(" maven:io.sentry:sentry-android", BuildConfig.SENTRY_ANDROID_VERSION)
        event
    }
}
