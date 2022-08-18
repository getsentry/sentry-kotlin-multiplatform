package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentryScope

actual abstract class BaseSentryScopeTest {
    actual fun initializeScope(): Scope {
        val cocoaScope = SentryScope()
        val scopeCocoaImpl = ScopeCocoaImpl(cocoaScope)
        return Scope(scopeCocoaImpl)
    }
}
