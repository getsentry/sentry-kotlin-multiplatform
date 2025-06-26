package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.applyKmpEvent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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

        assertEquals(1, nativeEvent.releaseSetCount)
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

        assertEquals(1, nativeEvent.distSetCount)
    }

    @Test
    fun `native release is set if kmp release has different value`() {
        val nativeEvent = FakeSentryEvent().apply {
            releaseName = "1.0.0"
        }
        val kmpEvent = SentryEvent().apply {
            release = "7.0.0"
        }

        nativeEvent.applyKmpEvent(kmpEvent)

        assertEquals(2, nativeEvent.releaseSetCount)
    }

    @Test
    fun `native dist is set if kmp dist has different value`() {
        val nativeEvent = FakeSentryEvent().apply {
            dist = "randomDist"
        }
        val kmpEvent = SentryEvent().apply {
            dist = "differentDist"
        }

        nativeEvent.applyKmpEvent(kmpEvent)

        assertEquals(2, nativeEvent.distSetCount)
    }
}

// init with a SentryLevel as a workaround for:
// Unable to call non-designated initializer as super constructor
private class FakeSentryEvent : CocoaSentryEvent(Internal.Sentry.kSentryLevelFatal) {
    var releaseSetCount = 0
    var distSetCount = 0

    override fun setReleaseName(releaseName: String?) {
        super.setReleaseName(releaseName)
        releaseSetCount += 1
    }

    override fun setDist(dist: String?) {
        super.setDist(dist)
        distSetCount += 1
    }
}
