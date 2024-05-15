package io.sentry.kotlin.multiplatform

import io.sentry.Sentry as JvmSentry

internal actual class SentryPlatformInstance : SentryInstance {
    override fun init(configuration: PlatformOptionsConfiguration) {
        JvmSentry.init(configuration)
    }
}
