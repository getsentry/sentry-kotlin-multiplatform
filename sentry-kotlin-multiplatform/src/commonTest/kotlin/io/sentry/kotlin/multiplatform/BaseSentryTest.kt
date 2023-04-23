package io.sentry.kotlin.multiplatform

expect abstract class BaseSentryTest() {
    val platform: String
    val authToken: String
    fun sentryInit(optionsConfiguration: OptionsConfiguration)
}
