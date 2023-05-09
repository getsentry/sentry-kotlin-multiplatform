package io.sentry.kotlin.multiplatform

internal object SentryLevelNumConstants {
    const val DEBUG_LEVEL = 1
    const val INFO_LEVEL = 2
    const val WARNING_LEVEL = 3
    const val ERROR_LEVEL = 4
    const val FATAL_LEVEL = 5
}

/** The level of the event similar to logging levels. */
public enum class SentryLevel(private val value: Int) {
    DEBUG(SentryLevelNumConstants.DEBUG_LEVEL),
    INFO(SentryLevelNumConstants.INFO_LEVEL),
    WARNING(SentryLevelNumConstants.WARNING_LEVEL),
    ERROR(SentryLevelNumConstants.ERROR_LEVEL),
    FATAL(SentryLevelNumConstants.FATAL_LEVEL);

    internal fun toInt(): Int {
        return this.value
    }

    internal companion object {
        fun fromInt(value: Int): SentryLevel? {
            return try {
                values().first {
                    it.value == value
                }
            } catch (throwable: Throwable) {
                null
            }
        }
    }
}
