package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.Sentry
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.RuntimeException

class SentryTestsJVM {

    @BeforeTest
    fun `before test - sdk init`() {
        Sentry.init("https://f7f320d5c3a54709be7b28e0f2ca7081@sentry.io/1808954")
    }

    @AfterTest
    fun `after test - sdk close`() {
        Sentry.close()
    }

    @Test
    fun `capture a message`() {
        Sentry.captureMessage("hello")
    }

    @Test
    fun `capture a exception`() {
        Sentry.captureException(RuntimeException("test"))
    }
}