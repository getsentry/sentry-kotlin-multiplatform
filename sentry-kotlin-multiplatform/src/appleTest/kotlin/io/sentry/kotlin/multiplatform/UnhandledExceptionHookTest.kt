package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.nsexception.wrapUnhandledExceptionHook
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertSame

class UnhandledExceptionHookTest {
    @Test
    fun `concurrent installations keep a single Sentry hook`() =
        runBlocking {
            val previous = setUnhandledExceptionHook(null)
            try {
                val ready = CompletableDeferred<Unit>()
                val installations =
                    (1..32).map {
                        async(Dispatchers.Default) {
                            ready.await()
                            wrapUnhandledExceptionHook { }
                            getUnhandledExceptionHook()
                        }
                    }
                ready.complete(Unit)
                val hooks = installations.awaitAll()
                hooks.forEach { assertSame(hooks.first(), it) }
                wrapUnhandledExceptionHook { }
                assertSame(hooks.first(), getUnhandledExceptionHook())
            } finally {
                setUnhandledExceptionHook(previous)
            }
        }
}
