package io.sentry.kotlin.multiplatform

expect abstract class BaseSentryScopeTest() {

    fun initializeScope(scope: SentryScope)
    fun syncFields(scope: SentryScope)
}

