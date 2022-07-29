package io.sentry.kotlin.multiplatform

expect class SentryContext(): ISentryContext {
    override fun setContext(key: String, value: Any)
}

interface ISentryContext {
    fun setContext(key: String, value: Any)
}
