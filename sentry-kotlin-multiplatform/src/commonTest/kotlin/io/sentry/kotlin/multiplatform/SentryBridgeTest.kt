package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.fakes.FakeSentryInstance
import io.sentry.kotlin.multiplatform.utils.fakeDsn
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SentryBridgeTest {
    private lateinit var fixture: Fixture

    @BeforeTest
    fun `set up`() {
        fixture = Fixture()
    }

    @Test
    fun `init sets correct configuration`() {
        // Given
        val configuration: OptionsConfiguration = {
            it.dsn = fakeDsn
            it.release = "1.0.0"
        }

        // When
        fixture.sut.init(configuration)

        // Then
        val expectedOptions = SentryOptions().apply(configuration)
        val actualOptions = SentryPlatformOptions().apply(fixture.sentryInstance.lastConfiguration!!).toSentryOptions()

        // Note: We don't test every single combination because creating a converter from
        // Platform options to SentryOptions for every single platform and every property is overkill
        // We test a few properties to make sure the conversion is working
        assertEquals(expectedOptions.dsn, actualOptions.dsn)
        assertEquals(expectedOptions.release, actualOptions.release)
        assertTrue(actualOptions.sdk!!.name.contains("kmp"))
        assertEquals(BuildKonfig.VERSION_NAME, actualOptions.sdk?.version)
    }
}

internal class Fixture {
    internal val sentryInstance = FakeSentryInstance()

    val sut get() = SentryBridge(sentryInstance)
}
