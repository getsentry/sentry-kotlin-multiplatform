package io.sentry.kotlin.multiplatform

actual abstract class BaseSentryScopeTest {
    actual fun initializeScope(): Scope {
        val cocoaScope = cocoapods.Sentry.SentryScope()
        val scopeCocoaImpl = ScopeCocoaImpl(cocoaScope)
        return Scope(scopeCocoaImpl)
    }
}
