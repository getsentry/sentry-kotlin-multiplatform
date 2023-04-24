package io.sentry.kotlin.multiplatform

actual class SentryOptionsConfigurator {
    actual fun applyOptions(optionsConfiguration: OptionsConfiguration): SentryEvent? {
        return SentryEvent()
    }

    actual fun applyOptions(options: SentryOptions): SentryEvent? {
        TODO("Not yet implemented")
    }
}
