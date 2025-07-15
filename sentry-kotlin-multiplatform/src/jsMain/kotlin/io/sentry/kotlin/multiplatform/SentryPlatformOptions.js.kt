package io.sentry.kotlin.multiplatform

public actual class SentryPlatformOptions {
    // Minimal stub – extend as needed
    var dsn: String? = null
}

internal actual fun SentryPlatformOptions.prepareForInit() {
    // no-op on JS
}

internal actual fun SentryOptions.toPlatformOptionsConfiguration(): PlatformOptionsConfiguration =
    { /* no-op for JS target */ }