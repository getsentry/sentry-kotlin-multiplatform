package io.sentry.kotlin.multiplatform

actual abstract class BaseSentryTest {
    actual val platform: String = "Apple"
    actual fun sentryInit(optionsConfiguration: OptionsConfiguration) {
        Sentry.init(optionsConfiguration)
    }
}
