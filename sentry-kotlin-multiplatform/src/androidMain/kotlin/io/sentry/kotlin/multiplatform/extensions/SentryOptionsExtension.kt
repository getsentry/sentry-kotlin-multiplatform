package io.sentry.kotlin.multiplatform.extensions

import io.sentry.android.core.SentryAndroidOptions as AndroidSentryOptions
import io.sentry.kotlin.multiplatform.SentryOptions

internal fun SentryOptions.toAndroidSentryOptions(): (AndroidSentryOptions) -> Unit = {
    it.applyJvmBaseOptions(this)

    it.isAttachScreenshot = this.attachScreenshot
}
