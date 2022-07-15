package io.sentry.kotlin.multiplatform

actual abstract class BaseSentryTest {
    val context: Any? = null

    val platform: String = "Apple"

}