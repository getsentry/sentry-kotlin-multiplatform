package io.sentry.kotlin.multiplatform.protocol

import io.sentry.Sentry
import io.sentry.kotlin.multiplatform.Sentry as SentryKmp
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.extensions.toJvmSentryOptions

/**
 * Sentry initialization with an option configuration handler.
 *
 * @param configuration Options configuration handler.
 */
fun SentryKmp.init(configuration: (SentryOptions) -> Unit) {
    val options = SentryOptions()
    configuration.invoke(options)
    Sentry.init(options.toJvmSentryOptions())
}
