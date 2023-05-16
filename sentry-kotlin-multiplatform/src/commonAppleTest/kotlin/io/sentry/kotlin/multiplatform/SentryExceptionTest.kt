package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toKmpSentryException
import io.sentry.kotlin.multiplatform.protocol.SentryException
import platform.Foundation.NSNumber
import kotlin.test.Test

class SentryExceptionTest {

    @Test
    fun `SentryException ThreadId NSNumber long conversion`() {
        val value = "testValue"
        val type = "type"
        val cocoaSentryException = CocoaSentryException(value = value, type = type).apply {
            threadId = NSNumber(long = 1)
        }
        val sentryException = SentryException(
            type = type,
            value = value,
            threadId = 1
        )
        assert(cocoaSentryException.toKmpSentryException() == sentryException)
    }

    @Test
    fun `SentryException ThreadId NSNumber longLong conversion`() {
        val value = "testValue"
        val type = "type"
        val cocoaSentryException = CocoaSentryException(value = value, type = type).apply {
            threadId = NSNumber(longLong = 1L)
        }
        val sentryException = SentryException(
            type = type,
            value = value,
            threadId = 1
        )
        assert(cocoaSentryException.toKmpSentryException() == sentryException)
    }

    @Test
    fun `SentryException ThreadId NSNumber int conversion`() {
        val value = "testValue"
        val type = "type"
        val cocoaSentryException = CocoaSentryException(value = value, type = type).apply {
            threadId = NSNumber(int = 1)
        }
        val sentryException = SentryException(
            type = type,
            value = value,
            threadId = 1
        )
        assert(cocoaSentryException.toKmpSentryException() == sentryException)
    }

    @Test
    fun `SentryException ThreadId NSNumber short conversion`() {
        val value = "testValue"
        val type = "type"
        val cocoaSentryException = CocoaSentryException(value = value, type = type).apply {
            threadId = NSNumber(short = 1)
        }
        val sentryException = SentryException(
            type = type,
            value = value,
            threadId = 1
        )
        assert(cocoaSentryException.toKmpSentryException() == sentryException)
    }

    @Test
    fun `SentryException ThreadId NSNumber null conversion`() {
        val value = "testValue"
        val type = "type"
        val cocoaSentryException = CocoaSentryException(value = value, type = type)
        val sentryException = SentryException(
            type = type,
            value = value
        )
        assert(cocoaSentryException.toKmpSentryException() == sentryException)
    }
}
