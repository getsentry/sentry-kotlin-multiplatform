package io.sentry.kotlin.multiplatform.extensions

import io.sentry.android.core.SentryAndroidOptions
import io.sentry.kotlin.multiplatform.SentryOptions

internal fun SentryOptions.toAndroidSentryOptionsCallback(): (SentryAndroidOptions) -> Unit = {
    it.applyJvmBaseOptions(this)
}
