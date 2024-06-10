package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentrySDK

internal actual class SentryPlatformInstance : SentryInstance {
    override fun init(configuration: PlatformOptionsConfiguration) {
        val finalConfiguration: (CocoaSentryOptions?) -> Unit = {
            if (it != null) {
                configuration(it)
            }
        }
        SentrySDK.start(finalConfiguration)
    }
}
