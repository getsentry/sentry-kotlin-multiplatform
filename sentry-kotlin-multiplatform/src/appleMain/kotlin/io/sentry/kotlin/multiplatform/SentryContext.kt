package io.sentry.kotlin.multiplatform

import platform.Foundation.NSDictionary
import platform.Foundation.setValue

actual class SentryContext: ISentryContext {

    private val context = NSDictionary()

    actual override fun setContext(key: String, value: Any) {
        context.setValue(value, key)
    }
}
