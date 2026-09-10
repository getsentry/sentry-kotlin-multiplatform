package io.sentry.kotlin.multiplatform

actual abstract class BaseSentryTest {
    actual val platform: String = "Apple"
    actual val authToken: String? = "fake-auth-token"
    actual fun sentryInit(optionsConfiguration: OptionsConfiguration) {
        Sentry.init(optionsConfiguration)
    }

    actual fun sentryInitWithPlatformOptions(platformOptionsConfiguration: PlatformOptionsConfiguration) {
        Sentry.initWithPlatformOptions(platformOptionsConfiguration)
    }
}
