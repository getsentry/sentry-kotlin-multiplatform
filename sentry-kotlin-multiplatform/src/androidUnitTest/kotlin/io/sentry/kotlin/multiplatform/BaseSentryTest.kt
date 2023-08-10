package io.sentry.kotlin.multiplatform

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import kotlin.test.BeforeTest

@RunWith(AndroidJUnit4::class)
actual abstract class BaseSentryTest {
    actual val platform: String = "Android"
    actual val authToken: String? = System.getenv("SENTRY_AUTH_TOKEN")
    actual fun sentryInit(optionsConfiguration: OptionsConfiguration) {
        Sentry.init(optionsConfiguration)
    }

    @BeforeTest
    open fun setUp() {
        // Set up the provider needed for Sentry.init on Android
        val provider = Robolectric.buildContentProvider(ContextProvider::class.java)
        provider.create()
    }
}
