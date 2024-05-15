package io.sentry.kotlin.multiplatform.fakes

import io.sentry.kotlin.multiplatform.PlatformOptionsConfiguration
import io.sentry.kotlin.multiplatform.SentryInstance

class FakeSentryInstance : SentryInstance {
    internal var lastConfiguration: PlatformOptionsConfiguration? = null

    override fun init(configuration: PlatformOptionsConfiguration) {
        lastConfiguration = configuration
    }
}
