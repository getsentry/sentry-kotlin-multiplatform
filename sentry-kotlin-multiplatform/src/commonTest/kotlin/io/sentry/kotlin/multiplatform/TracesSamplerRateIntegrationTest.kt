package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.utils.fakeDsn
import kotlin.test.Test
import kotlin.test.assertEquals

class TracesSamplerRateIntegrationTest: BaseSentryTest() {

    @Test
    fun `tracesSampler can receive correct TransactionContext name`() {
        val expectedName = "test"
        var actualName = ""
        sentryInit {
            it.dsn = fakeDsn
            it.tracesSampler = { context ->
                actualName = context.transactionContext.name
                null
            }
        }
        val transaction = Sentry.startTransaction("test", "testOperation")
        transaction.finish()
        assertEquals(expectedName, actualName)
    }

    @Test
    fun `tracesSampler can receive correct TransactionContext spanId`() {
        var actualSpanId = ""
        sentryInit {
            it.dsn = fakeDsn
            it.tracesSampler = { context ->
                actualSpanId = context.transactionContext.spanId.toString()
                null
            }
        }
        val transaction = Sentry.startTransaction("test", "testOperation")
        transaction.finish()
        assertEquals(transaction.spanId.toString(), actualSpanId)
    }

    @Test
    fun `tracesSampler can receive correct TransactionContext parentSpanId`() {
        var actualParentSpanId = ""
        sentryInit {
            it.dsn = fakeDsn
            it.tracesSampler = { context ->
                actualParentSpanId = context.transactionContext.parentSpanId.toString()
                null
            }
        }
        val transaction = Sentry.startTransaction("test", "testOperation")
        transaction.finish()
        assertEquals(transaction.parentSpanId?.toString(), actualParentSpanId)
    }

    @Test
    fun `tracesSampler can receive correct TransactionContext description`() {
        val expectedDescription = "test"
        var actualDescription: String? = ""
        sentryInit {
            it.dsn = fakeDsn
            it.tracesSampler = { context ->
                actualDescription = context.transactionContext.description
                null
            }
        }
        val transaction = Sentry.startTransaction("test", "testOperation")
        transaction.description = expectedDescription
        transaction.finish()
        assertEquals(expectedDescription, actualDescription)
    }

    @Test
    fun `tracesSampler can receive correct TransactionContext operation`() {
        val expectedOperation = "testOperation"
        var actualOperation = ""
        sentryInit {
            it.dsn = fakeDsn
            it.tracesSampler = { context ->
                actualOperation = context.transactionContext.operation
                null
            }
        }
        val transaction = Sentry.startTransaction("test", "testOperation")
        transaction.finish()
        assertEquals(expectedOperation, actualOperation)
    }

    @Test
    fun `tracesSampler can receive correct TransactionContext sampled`() {
        val expectedSampled = null
        var actualSampled: Boolean? = false
        sentryInit {
            it.dsn = fakeDsn
            it.tracesSampler = { context ->
                actualSampled = context.transactionContext.sampled
                null
            }
        }
        val transaction = Sentry.startTransaction("test", "testOperation")
        transaction.finish()
        assertEquals(expectedSampled, actualSampled)
    }

    @Test
    fun `tracesSampler can receive correct TransactionContext parentSampled`() {
        val expectedSampled = null
        var actualSampled: Boolean? = false
        sentryInit {
            it.dsn = fakeDsn
            it.tracesSampler = { context ->
                actualSampled = context.transactionContext.parentSampled
                null
            }
        }
        val transaction = Sentry.startTransaction("test", "testOperation")
        transaction.finish()
        assertEquals(expectedSampled, actualSampled)
    }
}