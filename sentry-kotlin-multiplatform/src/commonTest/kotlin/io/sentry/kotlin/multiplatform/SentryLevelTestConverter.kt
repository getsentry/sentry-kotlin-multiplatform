package io.sentry.kotlin.multiplatform

expect class SentryLevelTestConverter() {

    fun convert(sentryLevel: SentryLevel): SentryLevel
}
