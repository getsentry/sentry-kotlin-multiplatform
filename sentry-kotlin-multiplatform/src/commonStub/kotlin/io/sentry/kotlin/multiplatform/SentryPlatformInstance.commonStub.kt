package io.sentry.kotlin.multiplatform

internal actual class SentryPlatformInstance : SentryInstance {
    actual override fun init(configuration: PlatformOptionsConfiguration) {
    }
}
