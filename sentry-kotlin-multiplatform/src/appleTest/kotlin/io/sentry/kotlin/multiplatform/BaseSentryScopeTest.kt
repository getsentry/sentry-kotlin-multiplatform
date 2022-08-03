package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentryScope as CocoaScope

actual abstract class BaseSentryScopeTest {
    actual fun initializeScope(): SentryScope {
        val cocoaScope = CocoaScope()
        val scopeCocoaImpl = SentryScopeCocoaImpl(cocoaScope)
        return SentryScope(scopeCocoaImpl)
    }
}
