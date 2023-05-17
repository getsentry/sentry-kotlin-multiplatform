package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toKmpSentryException
import io.sentry.kotlin.multiplatform.protocol.SentryException
import kotlinx.cinterop.convert
import platform.Foundation.NSNumber
import kotlin.test.Test

class SentryExceptionTest {
    private val value = "testValue"
    private val type = "type"
    private val threadId = 1

    private fun getCocoaSentryException(): CocoaSentryException {
        return CocoaSentryException(value = value, type = type)
    }

    private fun getKmpSentryException(threadId: Long? = this.threadId.toLong()): SentryException {
        return SentryException(value = value, type = type, threadId = threadId)
    }

    @Test
    fun `SentryException ThreadId NSNumber long conversion`() {
        val cocoaSentryException = getCocoaSentryException().apply {
            threadId = NSNumber(long = this@SentryExceptionTest.threadId.convert())
        }
        val sentryException = getKmpSentryException()
        assert(cocoaSentryException.toKmpSentryException() == sentryException)
    }

    @Test
    fun `SentryException ThreadId NSNumber longLong conversion`() {
        val cocoaSentryException = getCocoaSentryException().apply {
            threadId = NSNumber(longLong = this@SentryExceptionTest.threadId.convert())
        }
        val sentryException = getKmpSentryException()
        assert(cocoaSentryException.toKmpSentryException() == sentryException)
    }

    @Test
    fun `SentryException ThreadId NSNumber int conversion`() {
        val cocoaSentryException = getCocoaSentryException().apply {
            threadId = NSNumber(int = this@SentryExceptionTest.threadId.convert())
        }
        val sentryException = getKmpSentryException()
        assert(cocoaSentryException.toKmpSentryException() == sentryException)
    }

    @Test
    fun `SentryException ThreadId NSNumber short conversion`() {
        val cocoaSentryException = getCocoaSentryException().apply {
            threadId = NSNumber(short = this@SentryExceptionTest.threadId.convert())
        }
        val sentryException = getKmpSentryException()
        assert(cocoaSentryException.toKmpSentryException() == sentryException)
    }

    @Test
    fun `SentryException ThreadId NSNumber null conversion`() {
        val cocoaSentryException = getCocoaSentryException()
        val sentryException = getKmpSentryException(threadId = null)
        assert(cocoaSentryException.toKmpSentryException() == sentryException)
    }
}
