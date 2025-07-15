package io.sentry.kotlin.multiplatform

internal actual class SentryPlatformInstance : SentryInstance {
    override fun init(configuration: PlatformOptionsConfiguration) {
        // Execute user configuration lambda with a fresh stub instance
        configuration(SentryPlatformOptions())
    }
}