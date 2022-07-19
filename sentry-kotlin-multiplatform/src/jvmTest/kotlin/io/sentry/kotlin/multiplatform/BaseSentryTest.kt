package io.sentry.kotlin.multiplatform

actual abstract class BaseSentryTest {
    actual val context: Any? = null

    actual val platform: String = "JVM"

    actual fun authToken(): String {
        return ""
    }
}