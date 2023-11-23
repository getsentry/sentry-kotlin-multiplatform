package io.sentry.kotlin.multiplatform

/** Base class for tests where Sentry.init is required. */
expect abstract class BaseSentryTest() {
    val platform: String
    val authToken: String?
    fun sentryInit(optionsConfiguration: OptionsConfiguration)
}
