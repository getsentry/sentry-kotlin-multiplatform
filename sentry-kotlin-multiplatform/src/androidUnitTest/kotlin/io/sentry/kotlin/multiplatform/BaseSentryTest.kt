package io.sentry.kotlin.multiplatform

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
actual abstract class BaseSentryTest {
    actual val platform: String = "Android"
    actual val authToken: String? = System.getenv("SENTRY_AUTH_TOKEN")
    actual fun sentryInit(optionsConfiguration: OptionsConfiguration) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        Sentry.init(context, optionsConfiguration)
    }
}
