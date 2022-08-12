package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentrySDK
import io.sentry.kotlin.multiplatform.extensions.toCocoaOptionsConfiguration

internal actual fun initCocoaTarget(configuration: (SentryOptions) -> Unit) {
    val options = SentryOptions()
    configuration.invoke(options)
    SentrySDK.start(options.toCocoaOptionsConfiguration())
}