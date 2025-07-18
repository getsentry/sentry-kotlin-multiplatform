package io.sentry.kotlin.multiplatform

import io.sentry.Sentry as JvmSentry

internal actual class SentryPlatformInstance : SentryInstance {
    actual override fun init(configuration: PlatformOptionsConfiguration) {
        JvmSentry.init(configuration)
    }
}
