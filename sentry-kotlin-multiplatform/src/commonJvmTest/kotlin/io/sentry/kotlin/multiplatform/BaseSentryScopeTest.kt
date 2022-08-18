package io.sentry.kotlin.multiplatform

import io.sentry.SentryOptions

actual abstract class BaseSentryScopeTest {
    actual fun initializeScope(): Scope {
        val androidScope = JvmScope(SentryOptions())
        val scopeAndroidImpl = ScopeJvmImpl(androidScope)
        return Scope(scopeAndroidImpl)
    }
}
