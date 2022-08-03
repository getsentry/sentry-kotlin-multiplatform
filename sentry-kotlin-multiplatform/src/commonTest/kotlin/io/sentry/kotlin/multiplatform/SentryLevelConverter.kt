package io.sentry.kotlin.multiplatform

expect class SentryLevelConverter(sentryLevel: SentryLevel) {

    fun getLevel(): SentryLevel
}
