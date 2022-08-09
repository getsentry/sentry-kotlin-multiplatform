package io.sentry.kotlin.multiplatform.extensions

import io.sentry.android.core.SentryAndroidOptions
import io.sentry.kotlin.multiplatform.SentryOptions

internal fun SentryOptions.toSentryAndroidOptions(): (SentryAndroidOptions) -> Unit = {
    it.applyBaseOptions(this)

    it.isAnrEnabled = this.anrEnabled
    it.anrTimeoutIntervalMillis = this.anrTimeoutIntervalMillis
    it.isAttachScreenshot = this.attachScreenshot
}
