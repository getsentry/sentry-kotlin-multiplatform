package io.sentry.kotlin.multiplatform

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

private const val DSN = "https://83f281ded2844eda83a8a413b080dbb9@o447951.ingest.sentry.io/5903800"

class SentryTests : BaseSentryTest() {

  @BeforeTest
  fun init() {
    super.beforeTest()
    Sentry.init(DSN)
  }

  @AfterTest
  fun close() {
    Sentry.close()
  }

  @Test
  fun testCapturesMessages() {
    Sentry.captureMessage("hello from $platform tests")
  }

  @Test
  fun testCapturesExceptions() {
    Sentry.captureException(RuntimeException("test"))
  }
}