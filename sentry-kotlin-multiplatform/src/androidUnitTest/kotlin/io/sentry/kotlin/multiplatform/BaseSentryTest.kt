package io.sentry.kotlin.multiplatform

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
actual abstract class BaseSentryTest {
    actual val platform: String = "Android"
    actual val authToken: String = System.getenv("AUTH_TOKEN") ?: ""
    actual fun sentryInit(optionsConfiguration: OptionsConfiguration) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        Sentry.init(context, optionsConfiguration)
    }
}
