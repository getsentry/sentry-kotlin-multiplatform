package io.sentry.kotlin.multiplatform

import io.sentry.Sentry
import io.sentry.kotlin.multiplatform.extensions.toJvmSentryOptionsCallback

internal actual fun initSentry(context: Context?, configuration: OptionsConfiguration) {
    val options = SentryOptions()
    configuration.invoke(options)
    Sentry.init(options.toJvmSentryOptionsCallback())
}

// The context is unused here and only implemented to satisfy the expect
public actual abstract class Context
