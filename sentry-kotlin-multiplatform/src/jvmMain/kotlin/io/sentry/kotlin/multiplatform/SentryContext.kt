package io.sentry.kotlin.multiplatform

import io.sentry.protocol.Contexts

actual class SentryContext: ISentryContext {

    private var context = Contexts()

    actual override fun setContext(key: String, value: Any) {
        context.put(key, value)
    }
}
