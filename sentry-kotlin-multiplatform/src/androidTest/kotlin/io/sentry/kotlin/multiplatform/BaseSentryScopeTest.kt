package io.sentry.kotlin.multiplatform

import io.sentry.SentryOptions

actual abstract class BaseSentryScopeTest {
    actual fun initializeScope(): SentryScope {
        val androidScope = AndroidScope(SentryOptions())
        val scopeAndroidImpl = ScopeAndroidImpl(androidScope)
        return SentryScope(scopeAndroidImpl)
    }
}
