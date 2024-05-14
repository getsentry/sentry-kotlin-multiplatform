package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentrySDK
import io.sentry.kotlin.multiplatform.extensions.toCocoaOptionsConfiguration

internal actual fun initSentry(configuration: OptionsConfiguration) {
    val options = SentryOptions()
    configuration.invoke(options)
    initSentryWithPlatformOptions(options.toCocoaOptionsConfiguration())
}

internal actual fun initSentryWithPlatformOptions(configuration: PlatformOptionsConfiguration) {
    val modifiedConfiguration: (SentryPlatformOptions?) -> Unit = { options ->
        if (options != null) {
            configuration(options)
            options.prepareForInit()
        }
    }
    finalizeSentryInit(modifiedConfiguration)
}

internal fun finalizeSentryInit(configuration: (SentryPlatformOptions?) -> Unit) {
    SentrySDK.startWithConfigureOptions(configuration)
}
