package io.sentry.kotlin.multiplatform

object SentryLevelNumConstants {
    const val DEBUG = 1
    const val INFO = 2
    const val WARNING = 3
    const val ERROR = 4
    const val FATAL = 5
}

enum class SentryLevel(val value: Int) {
    DEBUG(SentryLevelNumConstants.DEBUG),
    INFO(SentryLevelNumConstants.INFO),
    WARNING(SentryLevelNumConstants.WARNING),
    ERROR(SentryLevelNumConstants.ERROR),
    FATAL(SentryLevelNumConstants.FATAL);

    fun toInt(): Int {
        return this.value
    }

    companion object {
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
