package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.Sentry
import kotlin.test.Test

class SentryTests {
    @Test
    fun testCaptureMessage() {
        Sentry.init("https://8ee5199a90354faf995292b15c196d48@o19635.ingest.sentry.io/4394")
        Sentry.captureMessage("Message from tests.")
    }
}