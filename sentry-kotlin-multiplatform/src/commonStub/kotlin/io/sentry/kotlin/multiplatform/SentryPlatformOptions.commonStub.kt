package io.sentry.kotlin.multiplatform

public actual class SentryPlatformOptions

internal actual fun SentryPlatformOptions.prepareForInit() {
    // No-op
}

internal actual fun SentryOptions.toPlatformOptionsConfiguration(): PlatformOptionsConfiguration =
    {
            // No-op
    }
