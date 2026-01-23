package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.log.SentryLogEventBuilder
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

    override fun trace(block: SentryLogEventBuilder.() -> Unit) {
        // No-op
    }

    override fun debug(block: SentryLogEventBuilder.() -> Unit) {
        // No-op
    }

    override fun info(block: SentryLogEventBuilder.() -> Unit) {
        // No-op
    }

    override fun warn(block: SentryLogEventBuilder.() -> Unit) {
        // No-op
    }

    override fun error(block: SentryLogEventBuilder.() -> Unit) {
        // No-op
    }

    override fun fatal(block: SentryLogEventBuilder.() -> Unit) {
        // No-op
    }
}