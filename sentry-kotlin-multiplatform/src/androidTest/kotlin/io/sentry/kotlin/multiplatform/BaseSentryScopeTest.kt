package io.sentry.kotlin.multiplatform

import io.sentry.SentryOptions

actual abstract class BaseSentryScopeTest {
    actual fun initializeScope(scope: SentryScope) {
        val androidScope = AndroidScope(SentryOptions())
        scope.initWithAndroidScope(androidScope)
    }
}
