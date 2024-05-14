package io.sentry.kotlin.multiplatform

import kotlin.test.Test

/**
 * This tests the initialization of the SDK through [SentryPlatformOptions].
 */
class SentryPlatformOptionsTest {
    @Test
    fun `sdk name and version are modified after prepareInit`() {
        val options = SentryPlatformOptions()
        createSentryPlatformOptionsConfiguration().invoke(options)

        options.prepareForInit()

        options.assertSdkNameAndVersion()
    }
}
