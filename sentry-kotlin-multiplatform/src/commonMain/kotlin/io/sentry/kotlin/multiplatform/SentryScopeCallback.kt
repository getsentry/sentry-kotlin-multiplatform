package io.sentry.kotlin.multiplatform

fun interface SentryScopeCallback {
    fun run(scope: SentryScope)
}