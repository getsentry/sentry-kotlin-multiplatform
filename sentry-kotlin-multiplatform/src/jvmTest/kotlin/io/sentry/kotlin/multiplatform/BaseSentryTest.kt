package io.sentry.kotlin.multiplatform

actual abstract class BaseSentryTest {
    actual val platform: String = "JVM"
    actual val authToken: String? = System.getenv("SENTRY_AUTH_TOKEN")
    actual fun sentryInit(optionsConfiguration: OptionsConfiguration) {
        Sentry.init(optionsConfiguration)
    }
}
