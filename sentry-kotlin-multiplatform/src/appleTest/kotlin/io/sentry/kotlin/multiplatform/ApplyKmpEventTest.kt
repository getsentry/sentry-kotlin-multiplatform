package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.applyKmpEvent
import kotlin.test.Test
import kotlin.test.assertFalse

class ApplyKmpEventTest {
    @Test
    fun `native release is not set if kmp release has same value`() {
        val nativeEvent = FakeSentryEvent().apply {
            releaseName = "1.0.0"
        }
        val kmpEvent = SentryEvent().apply {
            release = "1.0.0"
        }

        nativeEvent.applyKmpEvent(kmpEvent)

        assertFalse(nativeEvent.wasReleaseSet)
    }

    @Test
    fun `native dist is not set if kmp dist has same value`() {
        val nativeEvent = FakeSentryEvent().apply {
            dist = "randomDist"
        }
        val kmpEvent = SentryEvent().apply {
            dist = "randomDist"
        }

        nativeEvent.applyKmpEvent(kmpEvent)

        assertFalse(nativeEvent.wasDistSet)
    }
}

// init with a SentryLevel as a workaround for:
// Unable to call non-designated initializer as super constructor
private class FakeSentryEvent : CocoaSentryEvent(Internal.Sentry.kSentryLevelFatal) {
    var wasReleaseSet = false
    var wasDistSet = false

    override fun setReleaseName(releaseName: String?) {
        super.setReleaseName(releaseName)
        wasReleaseSet = true
    }

    override fun setDist(dist: String?) {
        super.setDist(dist)
        wasDistSet = true
    }
}
