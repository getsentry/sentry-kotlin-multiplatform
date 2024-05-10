package io.sentry.kotlin.multiplatform

import io.sentry.Sentry
import io.sentry.kotlin.multiplatform.extensions.applyJvmBaseOptions
import io.sentry.kotlin.multiplatform.extensions.toJvmSentryOptionsCallback

internal actual fun initSentry(configuration: OptionsConfiguration) {
    val options = SentryOptions()
    configuration.invoke(options)
    Sentry.init(options.toJvmSentryOptionsCallback())
}

internal actual fun initSentryWithPlatformOptions(configuration: PlatformOptionsConfiguration) {
    Sentry.init { options ->
        configuration(options)
        options.sdkVersion?.name = BuildKonfig.SENTRY_KMP_JAVA_SDK_NAME
        options.sdkVersion?.version = BuildKonfig.VERSION_NAME
    }
}

// The context is unused here and only implemented to satisfy the expect
public actual abstract class Context
