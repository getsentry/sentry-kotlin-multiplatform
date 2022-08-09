package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmSentryOptions
import io.sentry.kotlin.multiplatform.SentryOptions

internal fun SentryOptions.toJvmSentryOptions(): (JvmSentryOptions) -> Unit = {
    it.applyBaseOptions(this)
}

/**
 * Applies the given options to this JvmSentryOption
 */
internal fun JvmSentryOptions.applyBaseOptions(options: SentryOptions) {
    this.dsn = options.dsn
    this.isAttachThreads = options.attachThreads
    this.isAttachStacktrace = options.attachStackTrace
    this.dist = options.dist
    this.environment = options.environment
}
