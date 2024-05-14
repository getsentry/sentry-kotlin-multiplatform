package io.sentry.kotlin.multiplatform

import kotlin.test.Test

class SentryPlatformOptionsTest {
    @Test
    fun `sdk name and version are modified after prepareInit`() {
        val options = SentryPlatformOptions()
        createSentryPlatformOptionsConfiguration().invoke(options)

        options.prepareForInit()

        options.assertSdkNameAndVersion()
    }
}
