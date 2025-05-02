package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.applyKmpEvent
import kotlin.test.Test
import kotlin.test.assertFalse

class ApplyKmpEventTest {
    @Test
    fun `native release is not set if kmp release has same value`() {
        val nativeEvent = FakeSentryEvent()
        val kmpEvent = SentryEvent()

        nativeEvent.applyKmpEvent(kmpEvent)

        assertFalse(nativeEvent.wasReleaseSet)
    }
}

// init with a SentryLevel as a workaround for:
// Unable to call non-designated initializer as super constructor
private class FakeSentryEvent : CocoaSentryEvent(Internal.Sentry.kSentryLevelFatal) {
    var wasReleaseSet = false

    override fun setReleaseName(releaseName: String?) {
        super.setReleaseName(releaseName)
        wasReleaseSet = true
    }
}