package io.sentry.kotlin.multiplatform

expect class SentryEventConfigurator() {
    val originalEvent: SentryEvent
    fun applyOptions(optionsConfiguration: OptionsConfiguration): SentryEvent?
    fun applyOptions(options: SentryOptions = SentryOptions()): SentryEvent?
}
