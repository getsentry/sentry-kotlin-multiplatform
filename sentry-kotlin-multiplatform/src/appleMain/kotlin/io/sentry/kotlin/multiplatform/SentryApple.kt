package io.sentry.kotlin.multiplatform

import Internal.Sentry.SentryCrashMonitorTypeCPPException
import Internal.Sentry.sentrycrash_setMonitoring
import Internal.Sentry.sentrycrashcm_getActiveMonitors
import cocoapods.Sentry.SentrySDK
import cocoapods.Sentry.startWithConfigureOptions
import io.sentry.kotlin.multiplatform.nsexception.setSentryUnhandledExceptionHook

/** Convenience extension to setup unhandled exception hook */
internal fun SentrySDK.Companion.start(configuration: (CocoaSentryOptions?) -> Unit) {
    setEnableUnhandledCppExceptionMonitoring(true)
    startWithConfigureOptions(configuration)
    if (!isUnhandledCppExceptionMonitoringEnabled()) {
        disableCppExceptionMonitor()
    }
    setSentryUnhandledExceptionHook()
}

private var enableUnhandledCppExceptionMonitoring = true

internal fun setEnableUnhandledCppExceptionMonitoring(enabled: Boolean) {
    enableUnhandledCppExceptionMonitoring = enabled
}

internal fun isUnhandledCppExceptionMonitoringEnabled(): Boolean = enableUnhandledCppExceptionMonitoring

private fun disableCppExceptionMonitor() {
    // Use the active mask: SentryCrashSwift exposes only a cached, read-only monitoring property.
    sentrycrash_setMonitoring(sentrycrashcm_getActiveMonitors() and SentryCrashMonitorTypeCPPException.inv())
}
