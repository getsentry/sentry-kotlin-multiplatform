package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentrySDK
import io.sentry.kotlin.multiplatform.extensions.toCocoaOptionsConfiguration

internal actual fun initSentry(configuration: OptionsConfiguration) {
    val options = SentryOptions()
    configuration.invoke(options)
    SentrySDK.start(options.toCocoaOptionsConfiguration())
}

internal actual fun initSentryWithPlatformOptions(configuration: PlatformOptionsConfiguration) {
    val options = CocoaSentryOptions()
    configuration.invoke(options)
    options.prepareForInit()
    SentrySDK.start(options)
}
