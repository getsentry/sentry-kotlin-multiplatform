package io.sentry.kotlin.multiplatform

expect class SentryLevelConverter() {

    fun convert(sentryLevel: SentryLevel): SentryLevel
}
