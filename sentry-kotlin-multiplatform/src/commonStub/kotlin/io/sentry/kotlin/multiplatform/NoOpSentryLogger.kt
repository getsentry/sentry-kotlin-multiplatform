package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.log.SentryLogBuilder
import io.sentry.kotlin.multiplatform.log.SentryLogger

/**
 * No-op implementation of [SentryLogger] for stub/unsupported platforms.
 */
internal class NoOpSentryLogger : SentryLogger {
    override fun trace(message: String, vararg args: Any?) {
        // No-op
    }

    override fun debug(message: String, vararg args: Any?) {
        // No-op
    }

    override fun info(message: String, vararg args: Any?) {
        // No-op
    }

    override fun warn(message: String, vararg args: Any?) {
        // No-op
    }

    override fun error(message: String, vararg args: Any?) {
        // No-op
    }

    override fun fatal(message: String, vararg args: Any?) {
        // No-op
    }

    override fun trace(block: SentryLogBuilder.() -> Unit) {
        // No-op
    }

    override fun debug(block: SentryLogBuilder.() -> Unit) {
        // No-op
    }

    override fun info(block: SentryLogBuilder.() -> Unit) {
        // No-op
    }

    override fun warn(block: SentryLogBuilder.() -> Unit) {
        // No-op
    }

    override fun error(block: SentryLogBuilder.() -> Unit) {
        // No-op
    }

    override fun fatal(block: SentryLogBuilder.() -> Unit) {
        // No-op
    }
}
