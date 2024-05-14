package io.sentry.kotlin.multiplatform

import kotlin.test.Test

/**
 * This tests the initialization of the SDK through [SentryPlatformOptions].
 */
class SentryPlatformInitTest {
    @Test
    fun testInitSentryWithPlatformOptions() {
        val foo = SentryPlatformOptionsFoo()
        foo.init()

        foo.assertSdkNameAndVersion()
    }
}
