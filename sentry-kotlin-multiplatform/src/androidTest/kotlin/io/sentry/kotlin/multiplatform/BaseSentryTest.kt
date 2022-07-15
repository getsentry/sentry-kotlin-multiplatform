package io.sentry.kotlin.multiplatform

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE, sdk = [28])
actual abstract class BaseSentryTest {
    val context: Any? = InstrumentationRegistry.getInstrumentation().targetContext

    val platform: String = "Android"

}