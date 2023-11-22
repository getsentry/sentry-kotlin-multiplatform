package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.applyCocoaBaseOptions

actual class SentryEventConfigurator {
  private val cocoaSentryEvent = CocoaSentryEvent()
  actual val originalEvent: SentryEvent = SentryEvent(cocoaSentryEvent)

  actual fun applyOptions(optionsConfiguration: OptionsConfiguration): SentryEvent? {
    val kmpOptions = SentryOptions()
    optionsConfiguration.invoke(kmpOptions)
    return applyOptions(kmpOptions)
  }

  actual fun applyOptions(options: SentryOptions): SentryEvent? {
    val cocoaOptions = CocoaSentryOptions()
    cocoaOptions.applyCocoaBaseOptions(options)
    val cocoaModifiedSentryEvent = cocoaOptions.beforeSend?.invoke(cocoaSentryEvent)
    return if (cocoaModifiedSentryEvent == null) {
      null
    } else {
      SentryEvent(cocoaModifiedSentryEvent)
    }
  }
}
