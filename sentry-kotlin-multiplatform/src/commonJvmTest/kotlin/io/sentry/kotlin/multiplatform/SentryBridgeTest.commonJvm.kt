package io.sentry.kotlin.multiplatform

import io.sentry.Hint
import io.sentry.kotlin.multiplatform.fakes.FakeSentryInstance
import io.sentry.kotlin.multiplatform.utils.fakeDsn
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

actual class SentryBridgeTest {
    private lateinit var fixture: Fixture

    @BeforeTest
    fun `set up`() {
        fixture = Fixture()
    }

    @Test
    actual fun `init sets correct configuration`() {
        // Given
        val configuration: OptionsConfiguration = {
            it.dsn = fakeDsn
            it.release = "1.0.0"
        }

        // When
        fixture.sut.init(configuration)

        // Then
        val expectedOptions = SentryOptions().apply(configuration)
        val actualOptions =
            SentryPlatformOptions().apply(fixture.sentryInstance.lastConfiguration!!) as JvmSentryOptions

        // Note: We don't test every single combination because creating a converter from
        // Platform options to SentryOptions for every single platform and every property is overkill
        // We test a few properties to make sure the conversion is working
        assertEquals(expectedOptions.dsn, actualOptions.dsn)
        assertEquals(expectedOptions.release, actualOptions.release)
    }

    @Test
    actual fun `setting null in beforeSend during init drops the event`() {
        // GIVEN
        fixture.sut.init {
            it.beforeSend = {
                null
            }
        }

        // WHEN
        val option = SentryPlatformOptions().apply {
            fixture.sentryInstance.lastConfiguration?.invoke(this)
        }.let { it as JvmSentryOptions }

        // THEN
        assert(option.beforeSend != null)
        assert(option.beforeSend!!.execute(JvmSentryEvent(), Hint()) == null)
    }

    @Test
    actual fun `default beforeSend in init does not drop the event`() {
        // GIVEN
        fixture.sut.init { }

        // WHEN
        val option = SentryPlatformOptions().apply {
            fixture.sentryInstance.lastConfiguration?.invoke(this)
        }.let { it as JvmSentryOptions }

        // THEN
        assert(option.beforeSend != null)
        assert(option.beforeSend!!.execute(JvmSentryEvent(), Hint()) != null)
    }

    @Test
    actual fun `init sets the SDK packages`() {
        // WHEN
        fixture.sut.init { }
        val option = SentryPlatformOptions().apply {
            fixture.sentryInstance.lastConfiguration?.invoke(this)
        }.let { it as JvmSentryOptions }

        // THEN
        assert(option.sdkVersion?.packageSet != null)
        assert(option.sdkVersion?.packageSet!!.isNotEmpty())
        assert(option.sdkVersion?.packageSet!!.find { it.name.contains("sentry") } != null)
    }

    @Test
    actual fun `init sets SDK version and name`() {
        // Given
        val configuration: OptionsConfiguration = {
            it.dsn = fakeDsn
            it.release = "1.0.0"
        }

        // When
        fixture.sut.init(configuration)
        val option = SentryPlatformOptions().apply {
            fixture.sentryInstance.lastConfiguration?.invoke(this)
        }.let { it as JvmSentryOptions }

        // Then
        assertTrue(option.sdkVersion!!.name.contains("kmp"))
        assertEquals(option.sdkVersion!!.version, BuildKonfig.VERSION_NAME)
    }

    internal class Fixture {
        val sentryInstance = FakeSentryInstance()

        val sut get() = SentryBridge(sentryInstance)
    }
}
