package io.sentry.kotlin.multiplatform

import io.sentry.Sentry
import io.sentry.kotlin.multiplatform.extensions.setSdkVersionAndName
import io.sentry.kotlin.multiplatform.extensions.toJvmSentryOptionsCallback

internal actual fun initSentry(configuration: OptionsConfiguration) {
    val options = SentryOptions()
    configuration.invoke(options)
    Sentry.init(options.toJvmSentryOptionsCallback())
}

internal actual fun initSentryWithPlatformOptions(configuration: PlatformOptionsConfiguration) {
    Sentry.init { options ->
        configuration(options)
        // We set the SDK name and version here because the user creates the native options directly
        // which means it will not contain the correct KMP SDK name and version
        options.setSdkVersionAndName()
    }
}

// The context is unused here and only implemented to satisfy the expect
public actual abstract class Context
