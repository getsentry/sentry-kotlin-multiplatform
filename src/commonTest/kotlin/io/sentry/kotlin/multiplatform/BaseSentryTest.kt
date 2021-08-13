package io.sentry.kotlin.multiplatform

expect abstract class BaseSentryTest() {

  fun beforeTest()

  val platform: String
}
