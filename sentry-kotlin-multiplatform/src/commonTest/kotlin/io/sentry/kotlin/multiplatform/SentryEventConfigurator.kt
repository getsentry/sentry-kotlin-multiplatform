package io.sentry.kotlin.multiplatform

/**
 * This class deals with configuring and modifying a SentryEvent. It is used to test any code that
 * can alter a SentryEvent.
 */
expect class SentryEventConfigurator() {
  val originalEvent: SentryEvent

  fun applyOptions(optionsConfiguration: OptionsConfiguration): SentryEvent?

  fun applyOptions(options: SentryOptions = SentryOptions()): SentryEvent?
}
