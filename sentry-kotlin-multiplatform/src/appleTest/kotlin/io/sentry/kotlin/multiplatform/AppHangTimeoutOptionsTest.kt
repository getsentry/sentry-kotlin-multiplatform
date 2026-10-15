package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toCocoaOptionsConfiguration
import kotlin.test.Test
import kotlin.test.assertEquals

class AppHangTimeoutOptionsTest {
    private fun cocoaAppHangTimeoutInterval(kmpMillis: Long? = null): Double {
        val nativeOptions = CocoaSentryOptions()
        SentryOptions()
            .apply { kmpMillis?.let { appHangTimeoutIntervalMillis = it } }
            .toCocoaOptionsConfiguration()
            .invoke(nativeOptions)
        return nativeOptions.appHangTimeoutInterval()
    }

    @Test
    fun `default app hang timeout maps to Cocoa's default of 2 seconds`() {
        assertEquals(2.0, cocoaAppHangTimeoutInterval())
    }

    @Test
    fun `app hang timeout in milliseconds maps to Cocoa's interval in seconds`() {
        assertEquals(1.5, cocoaAppHangTimeoutInterval(kmpMillis = 1500L))
        assertEquals(0.25, cocoaAppHangTimeoutInterval(kmpMillis = 250L))
    }
}
