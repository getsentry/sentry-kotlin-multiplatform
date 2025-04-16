package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.fakes.FakeSentryInstance
import junit.framework.TestCase.assertEquals
import kotlin.test.BeforeTest
import kotlin.test.Test

class SentryAndroidBridgeTest {
    private lateinit var fixture: Fixture

    @BeforeTest
    fun setup() {
        fixture = Fixture()
    }

    @Test
    fun `init sets native android sdk name`() {
        val sut = fixture.getSut()

        sut.init { }

        val option = SentryPlatformOptions().apply {
            fixture.sentryInstance.lastConfiguration?.invoke(this)
        }

        assertEquals(BuildKonfig.SENTRY_KMP_NATIVE_ANDROID_SDK_NAME, option.nativeSdkName)
    }
}

internal class Fixture {
    val sentryInstance = FakeSentryInstance()

    fun getSut(): SentryBridge {
        return SentryBridge(sentryInstance)
    }
}
