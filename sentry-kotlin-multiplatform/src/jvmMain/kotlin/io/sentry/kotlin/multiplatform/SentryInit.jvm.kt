package io.sentry.kotlin.multiplatform

import io.sentry.Sentry
import io.sentry.kotlin.multiplatform.extensions.toJvmSentryOptionsCallback

internal actual fun initSentry(configuration: OptionsConfiguration) {
    val options = SentryOptions()
    configuration.invoke(options)
    initSentryWithPlatformOptions(options.toJvmSentryOptionsCallback())
}

internal actual fun initSentryWithPlatformOptions(configuration: PlatformOptionsConfiguration) {
    val modifiedConfiguration: PlatformOptionsConfiguration = { options ->
        configuration(options)
        options.prepareForInit()
    }

    finalizeSentryInit(modifiedConfiguration)
}

internal fun finalizeSentryInit(configuration: PlatformOptionsConfiguration) {
    Sentry.init(configuration)
}

// The context is unused here and only implemented to satisfy the expect
public actual abstract class Context
