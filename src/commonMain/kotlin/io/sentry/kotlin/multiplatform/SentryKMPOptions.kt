package io.sentry.kotlin.multiplatform

open class SentryKMPOptions {
    var dsn: String? = null
    var attachStackTrace = true
    var attachThreads = true
}