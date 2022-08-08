package io.sentry.kotlin.multiplatform

open class SentryOptions {
    var dsn: String? = null
    var attachStackTrace = true
    var attachThreads = true
}
