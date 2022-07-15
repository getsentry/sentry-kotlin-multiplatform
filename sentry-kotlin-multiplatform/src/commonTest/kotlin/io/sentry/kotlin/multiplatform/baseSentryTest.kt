package io.sentry.kotlin.multiplatform

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

private const val dsn = "https://83f281ded2844eda83a8a413b080dbb9@o447951.ingest.sentry.io/5903800"

class SentryTests: BaseSentryTest() {
    @BeforeTest
    fun init() {
        SentryKMP.start(context) {
            it.dsn = dsn
        }
    }

    @Test
    fun captureMessage() {
        SentryKMP.captureMessage("Test running on platform $platform")
    }

    @Test
    fun captureException() {
        SentryKMP.captureException(IllegalArgumentException("Test exception on platform $platform"))
    }

    @AfterTest
    fun close() {
        SentryKMP.close()
    }
}

expect abstract class BaseSentryTest() {
    val context: Any?
    val platform: String
}