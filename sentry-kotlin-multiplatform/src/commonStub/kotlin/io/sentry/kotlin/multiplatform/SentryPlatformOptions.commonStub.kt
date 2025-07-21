package io.sentry.kotlin.multiplatform

public actual class SentryPlatformOptions

internal actual fun SentryPlatformOptions.prepareForInit() {
    // no-op on JS
}

internal actual fun SentryOptions.toPlatformOptionsConfiguration(): PlatformOptionsConfiguration =
    { /* no-op for JS target */ }
