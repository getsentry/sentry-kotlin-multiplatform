package io.sentry.kotlin.multiplatform

import io.sentry.SentryOptions

actual abstract class BaseSentryScopeTest {
    actual fun initializeScope(): Scope {
        val jvmScope = JvmScope(SentryOptions())
        return JvmScopeProvider(jvmScope)
    }
}
