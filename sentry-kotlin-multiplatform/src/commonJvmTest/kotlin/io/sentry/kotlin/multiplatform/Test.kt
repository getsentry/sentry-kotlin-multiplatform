package io.sentry.kotlin.multiplatform

import io.sentry.Hint
import io.sentry.ISentryClient
import io.sentry.ProfilingTraceData
import io.sentry.Scope
import io.sentry.SentryEnvelope
import io.sentry.SentryEvent
import io.sentry.Session
import io.sentry.TraceContext
import io.sentry.UserFeedback
import io.sentry.protocol.SentryId
import io.sentry.protocol.SentryTransaction
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.test.Test
import kotlin.test.assertTrue

typealias JvmSentry = io.sentry.Sentry

class cl : ISentryClient {
    override fun isEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun captureEvent(event: SentryEvent, scope: Scope?, hint: Hint?): SentryId {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }

    override fun flush(timeoutMillis: Long) {
        TODO("Not yet implemented")
    }

    override fun captureUserFeedback(userFeedback: UserFeedback) {
        TODO("Not yet implemented")
    }

    override fun captureSession(session: Session, hint: Hint?) {
        TODO("Not yet implemented")
    }

    override fun captureEnvelope(envelope: SentryEnvelope, hint: Hint?): SentryId? {
        TODO("Not yet implemented")
    }

    override fun captureTransaction(
        transaction: SentryTransaction,
        traceContext: TraceContext?,
        scope: Scope?,
        hint: Hint?,
        profilingTraceData: ProfilingTraceData?
    ): SentryId {
        TODO("Not yet implemented")
    }
}

class Test {
    private suspend fun captureMessageAndWait(): Boolean {
        return suspendCoroutine { continuation ->
            val a = Sentry.captureMessage("hello")
            println(a)
            GlobalScope.launch {
                delay(10000)
                continuation.resume(true)
            }
        }
    }

    @Test
    fun `SentryInit Test`() = runBlocking {
        Sentry.init {
            it.dsn = "https://13f281ded2844eda83a8a413b080dbb9@o447951.ingest.sentry.io/5903800"
        }

        val captureComplete = captureMessageAndWait()
        assertTrue(captureComplete)

        val a = JvmSentry.getLastEventId()
        println(a)
    }
}
