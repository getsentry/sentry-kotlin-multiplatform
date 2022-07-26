package io.sentry.kotlin.multiplatform

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class SentryTests {

    @BeforeTest
    fun setup() {
        SentryKMP.start {
            it.dsn = "https://26ba9de3c0c247f4b2c3e4ee646f2ebb@o1249351.ingest.sentry.io/6587139"
        }
    }

    @Test
    fun send() {
        runBlocking {
            delay(5000)
        }
        SentryKMP.captureMessage("send")
    }

    @AfterTest
    fun tearDown() {
        SentryKMP.close()
    }
}