package io.sentry.kotlin.multiplatform

expect class SentryOptionsConfigurator() {
    fun applyOptions(optionsConfiguration: OptionsConfiguration): SentryEvent?
    fun applyOptions(options: SentryOptions = SentryOptions()): SentryEvent?
}
