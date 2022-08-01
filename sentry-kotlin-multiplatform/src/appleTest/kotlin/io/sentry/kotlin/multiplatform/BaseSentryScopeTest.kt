package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentryScope as CocoaScope

actual abstract class BaseSentryScopeTest {
    actual fun initializeScope(scope: SentryScope) {
        val cocoaScope = CocoaScope()
        scope.initWithCocoaScope(cocoaScope)
    }

    actual fun syncFields(scope: SentryScope) {
        scope.syncFields()
    }

}
