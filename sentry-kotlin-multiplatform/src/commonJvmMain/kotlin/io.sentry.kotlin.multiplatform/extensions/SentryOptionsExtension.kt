package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmSentryOptions
import io.sentry.kotlin.multiplatform.SentryOptions

internal fun SentryOptions.toJvmSentryOptionsCallback(): (JvmSentryOptions) -> Unit = {
    it.applyJvmBaseOptions(this)
}

/**
 * Applies the given base SentryOptions to this JvmSentryOption
 * This allows separation of SentryAndroidOptions and SentryOptions
 */
internal fun JvmSentryOptions.applyJvmBaseOptions(options: SentryOptions) {
    this.dsn = options.dsn
    this.isAttachThreads = options.attachThreads
    this.isAttachStacktrace = options.attachStackTrace
    this.dist = options.dist
    this.environment = options.environment
}
