package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentrySDK
import io.sentry.kotlin.multiplatform.extensions.toCocoaOptionsConfiguration

internal actual fun initCocoaTarget(configuration: OptionsConfiguration) {
    val options = SentryOptions()
    configuration.invoke(options)
    SentrySDK.start(options.toCocoaOptionsConfiguration())
}
