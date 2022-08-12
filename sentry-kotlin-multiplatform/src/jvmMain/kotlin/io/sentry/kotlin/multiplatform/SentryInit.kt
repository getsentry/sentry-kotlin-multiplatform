package io.sentry.kotlin.multiplatform

import io.sentry.Sentry
import io.sentry.kotlin.multiplatform.extensions.toJvmSentryOptionsCallback

actual abstract class Context

actual fun initJvmTarget(context: Context?, configuration: (SentryOptions) -> Unit) {
    val options = SentryOptions()
    configuration.invoke(options)
    Sentry.init(options.toJvmSentryOptionsCallback())
}

