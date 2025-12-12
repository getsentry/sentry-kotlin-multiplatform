package io.sentry.kotlin.multiplatform

/**
 * No-op implementation of [SentryLoggerApi] for stub/unsupported platforms.
 */
internal class NoOpSentryLogger : SentryLoggerApi {
    override fun trace(message: String?, vararg args: Any?) {
        // No-op
    }

    override fun debug(message: String?, vararg args: Any?) {
        // No-op
    }

    override fun info(message: String?, vararg args: Any?) {
        // No-op
    }

    override fun warn(message: String?, vararg args: Any?) {
        // No-op
    }

    override fun error(message: String?, vararg args: Any?) {
        // No-op
    }

    override fun fatal(message: String?, vararg args: Any?) {
        // No-op
    }

    override fun log(level: SentryLogLevel, message: String?, vararg args: Any?) {
        // No-op
    }
}

internal actual fun loggerFactory(): SentryLoggerApi {
    return NoOpSentryLogger()
}