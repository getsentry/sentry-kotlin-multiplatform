package io.sentry.kotlin.multiplatform

actual abstract class BaseSentryTest {

  actual fun beforeTest() = Unit

  actual val platform: String get() = "Apple"
}
