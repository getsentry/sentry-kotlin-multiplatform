package io.sentry.kotlin.multiplatform

import io.sentry.android.core.SentryAndroid

internal actual class SentryPlatformInstance : SentryInstance {
    override fun init(configuration: PlatformOptionsConfiguration) {
        val context = applicationContext ?: run {
            // TODO: add logging later
            return
        }

        SentryAndroid.init(context, configuration)
    }
}
