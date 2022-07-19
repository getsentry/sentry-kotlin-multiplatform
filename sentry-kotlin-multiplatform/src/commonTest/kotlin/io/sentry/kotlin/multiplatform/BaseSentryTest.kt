package io.sentry.kotlin.multiplatform

expect abstract class BaseSentryTest() {
    val context: Any?
    val platform: String
    fun authToken(): String
}