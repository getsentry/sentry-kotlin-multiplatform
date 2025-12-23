package io.sentry.kotlin.multiplatform.log

import io.sentry.kotlin.multiplatform.SentryAttributes

/**
 * Represents a structured log entry.
 */
public open class SentryLog(
    public open val timestamp: Double,
    public open var level: SentryLogLevel,
    public open var body: String,
    public open var severityNumber: Int? = null,
    public open val attributes: SentryAttributes = SentryAttributes()
)
