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
    // We set the SDK name and version here because the user creates the native options directly
    // which means it will not contain the correct KMP SDK name and version
    setSdkVersionAndName()
    SentrySDK.start(options)
}
