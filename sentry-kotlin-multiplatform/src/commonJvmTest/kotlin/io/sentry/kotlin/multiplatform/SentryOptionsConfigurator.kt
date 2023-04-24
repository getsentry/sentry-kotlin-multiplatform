package io.sentry.kotlin.multiplatform

import io.sentry.Hint
import io.sentry.kotlin.multiplatform.extensions.applyJvmBaseOptions

actual class SentryOptionsConfigurator {
    actual fun applyOptions(optionsConfiguration: OptionsConfiguration): SentryEvent? {
        val kmpOptions = SentryOptions()
        optionsConfiguration.invoke(kmpOptions)
        return applyOptions(kmpOptions)
    }

    actual fun applyOptions(options: SentryOptions): SentryEvent? {
        val jvmOptions = JvmSentryOptions()
        jvmOptions.applyJvmBaseOptions(options)
        val jvmSentryEvent = JvmSentryEvent()
        val jvmHint = Hint()
        val jvmModifiedSentryEvent = jvmOptions.beforeSend?.execute(jvmSentryEvent, jvmHint)
        return if (jvmModifiedSentryEvent == null) {
            null
        } else {
            SentryEvent(jvmModifiedSentryEvent)
        }
    }
}
