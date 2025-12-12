package io.sentry.kotlin.multiplatform

internal actual fun loggerFactory(): SentryLoggerApi {
    return JvmSentryLogger(io.sentry.Sentry.logger())
}