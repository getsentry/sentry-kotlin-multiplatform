package io.sentry.kotlin.multiplatform

enum class SentryLevel(val value: Int) {
    DEBUG(1),
    INFO(2),
    WARNING(3),
    ERROR(4),
    FATAL(5);

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
