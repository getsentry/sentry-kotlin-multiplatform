package io.sentry.kotlin.multiplatform

import Internal.Sentry.SentryCrashMonitorTypeCPPException
import Internal.Sentry.SentryDependencyContainer
import cocoapods.Sentry.SentrySDK
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
    val crashReporter = SentryDependencyContainer.sharedInstance().crashReporter
    crashReporter.monitoring = crashReporter.monitoring and SentryCrashMonitorTypeCPPException.inv()
}
