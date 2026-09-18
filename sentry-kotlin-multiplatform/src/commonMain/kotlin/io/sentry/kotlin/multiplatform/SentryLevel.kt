package io.sentry.kotlin.multiplatform

internal object SentryLevelNumConstants {
    const val DEBUG_LEVEL = 1
    const val INFO_LEVEL = 2
    const val WARNING_LEVEL = 3
    const val ERROR_LEVEL = 4
    const val FATAL_LEVEL = 5
}

/** The level of the event similar to logging levels. */
public enum class SentryLevel(
    private val value: Int,
) {
    /** Diagnostic information for debugging. */
    DEBUG(SentryLevelNumConstants.DEBUG_LEVEL),

    /** Information about normal application activity. */
    INFO(SentryLevelNumConstants.INFO_LEVEL),

    /** Potential problems that do not prevent continued operation. */
    WARNING(SentryLevelNumConstants.WARNING_LEVEL),

    /** Failures that prevent an operation from completing. */
    ERROR(SentryLevelNumConstants.ERROR_LEVEL),

    /** Critical failures that prevent normal application operation. */
    FATAL(SentryLevelNumConstants.FATAL_LEVEL),
    ;

    internal fun toInt(): Int = this.value

    internal companion object {
        fun fromInt(value: Int): SentryLevel? =
            try {
                values().first {
                    it.value == value
                }
            } catch (throwable: Throwable) {
                null
            }
    }
}
