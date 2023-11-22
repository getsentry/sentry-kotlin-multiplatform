package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.utils.FAKE_DSN
import kotlin.test.Test
import kotlin.test.assertEquals

class TransactionIntegrationTest : BaseSentryTest() {
  @Test
  fun `tracesSampler can receive correct TransactionContext name`() {
    val expectedName = "test"
    var actualName = ""
    sentryInit {
      it.dsn = FAKE_DSN
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
  fun `tracesSampler can receive correct TransactionContext description`() {
    var receivedDescription: String? = ""
    sentryInit {
      it.dsn = FAKE_DSN
      it.tracesSampler = { context ->
        receivedDescription = context.transactionContext.description
        null
      }
    }
    val transaction = Sentry.startTransaction("test", "testOperation")
    transaction.finish()
    assertEquals(transaction.getDescription(), receivedDescription)
  }

  @Test
  fun `parentSpanId is correct after starting child span`() {
    sentryInit { it.dsn = FAKE_DSN }
    val transaction = Sentry.startTransaction("test", "testOperation")
    val child = transaction.startChild("child")
    child.finish()
    transaction.finish()
    // TODO
    //    assertEquals(child.getParentSpanId()?.toString(), transaction.getSpanId().toString())
  }

  @Test
  fun `getSpan returns the current active span with bindToScope enabled`() {
    sentryInit { it.dsn = FAKE_DSN }
    val transaction = Sentry.startTransaction("test", "testOperation", bindToScope = true)
    val activeTransactionSpanId = Sentry.getSpan()?.toSentryTrace()?.getSpanId()
    transaction.finish()
    assertEquals(
        activeTransactionSpanId,
        transaction.toSentryTrace().getSpanId(),
        "activeTransactionSpanId should be equal to transaction.spanId")
  }

  @Test
  fun `getSpan returns null when bindToScope is disabled`() {
    sentryInit { it.dsn = FAKE_DSN }
    val transaction = Sentry.startTransaction("test", "testOperation", bindToScope = false)
    val activeTransactionSpanId = (Sentry.getSpan() as? Span)?.getSpanId()
    val child = transaction.startChild("child")
    val activeChildSpanId = (Sentry.getSpan() as? Span)?.getSpanId()
    child.finish()
    transaction.finish()
    assertEquals(activeTransactionSpanId, null)
    assertEquals(activeChildSpanId, null)
  }

  @Test
  fun `getSpan returns null when Transaction has finished`() {
    sentryInit { it.dsn = FAKE_DSN }
    val transaction = Sentry.startTransaction("test", "testOperation", bindToScope = true)
    val activeTransactionSpanId = Sentry.getSpan()?.toSentryTrace()?.getSpanId()
    transaction.finish()
    val activeTransactionSpanIdAfterFinish = Sentry.getSpan()?.toSentryTrace()?.getSpanId()
    assertEquals(activeTransactionSpanId, transaction.toSentryTrace().getSpanId())
    assertEquals(activeTransactionSpanIdAfterFinish, null)
  }

  @Test
  fun `tracesSampler can receive correct TransactionContext operation`() {
    val expectedOperation = "testOperation"
    var actualOperation = ""
    sentryInit {
      it.dsn = FAKE_DSN
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
    val expectedSampled = false
    var actualSampled: Boolean? = null
    sentryInit {
      it.dsn = FAKE_DSN
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
    val expectedSampled = false
    var actualSampled: Boolean? = null
    sentryInit {
      it.dsn = FAKE_DSN
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
