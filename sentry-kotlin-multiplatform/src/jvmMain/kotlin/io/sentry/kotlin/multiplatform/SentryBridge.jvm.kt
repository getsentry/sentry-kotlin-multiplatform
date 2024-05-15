package io.sentry.kotlin.multiplatform

internal actual fun SentryPlatformOptions.prepareForInitBridge() {
    prepareForInit()
}
