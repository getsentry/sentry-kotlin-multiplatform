package io.sentry.kotlin.multiplatform

expect abstract class BaseSentryTest() {
    val platform: String
    fun sentryInit(optionsConfiguration: OptionsConfiguration)
}
