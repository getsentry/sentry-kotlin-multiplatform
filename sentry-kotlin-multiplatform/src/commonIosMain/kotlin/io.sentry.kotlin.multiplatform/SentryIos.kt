package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentrySDK
import io.sentry.kotlin.multiplatform.extensions.toIosOptionsConfiguration

actual fun Sentry.start(configuration: (SentryOptions) -> Unit) {
    val options = SentryOptions()
    configuration.invoke(options)
    SentrySDK.start(options.toIosOptionsConfiguration())
}
