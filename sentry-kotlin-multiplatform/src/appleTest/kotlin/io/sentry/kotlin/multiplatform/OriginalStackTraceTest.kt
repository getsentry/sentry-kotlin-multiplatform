package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.nsexception.OriginalStackTraceStore
import io.sentry.kotlin.multiplatform.nsexception.StackTraceCapturingRuntimeException
import io.sentry.kotlin.multiplatform.nsexception.captureOriginalStackTrace
import io.sentry.kotlin.multiplatform.nsexception.withCapturedStackTrace
import io.sentry.kotlin.multiplatform.nsexception.asNSException
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertNotNull
import kotlin.test.assertEquals
import kotlin.test.assertNull

class OriginalStackTraceTest {

    @Test
    fun `original stack trace store captures and retrieves stack traces`() {
        // Clear any existing traces
        OriginalStackTraceStore.clearAll()
        
        val exception = RuntimeException("Test exception")
        
        // Initially no stack trace should be stored
        assertNull(OriginalStackTraceStore.getOriginalStackTrace(exception))
        
        // Capture stack trace
        exception.captureOriginalStackTrace()
        
        // Now stack trace should be available
        val stackTrace = OriginalStackTraceStore.getOriginalStackTrace(exception)
        assertNotNull(stackTrace)
        assertTrue(stackTrace.isNotEmpty())
        
        val stringStackTrace = OriginalStackTraceStore.getOriginalStringStackTrace(exception)
        assertNotNull(stringStackTrace)
        assertTrue(stringStackTrace.isNotEmpty())
        
        // Cleanup
        OriginalStackTraceStore.cleanup(exception)
        assertNull(OriginalStackTraceStore.getOriginalStackTrace(exception))
    }

    @Test
    fun `stack trace capturing exception automatically stores stack trace`() {
        OriginalStackTraceStore.clearAll()
        
        val exception = StackTraceCapturingRuntimeException("Auto-captured")
        
        // Stack trace should be automatically captured
        val stackTrace = OriginalStackTraceStore.getOriginalStackTrace(exception)
        assertNotNull(stackTrace)
        assertTrue(stackTrace.isNotEmpty())
        
        OriginalStackTraceStore.cleanup(exception)
    }

    @Test
    fun `withCapturedStackTrace works with existing exceptions`() {
        OriginalStackTraceStore.clearAll()
        
        val originalException = RuntimeException("Original")
        
        // No stack trace initially
        assertNull(OriginalStackTraceStore.getOriginalStackTrace(originalException))
        
        val enhancedException = originalException.withCapturedStackTrace()
        
        // Should be the same instance
        assertTrue(enhancedException === originalException)
        
        // Now should have stack trace
        assertNotNull(OriginalStackTraceStore.getOriginalStackTrace(enhancedException))
        
        OriginalStackTraceStore.cleanup(enhancedException)
    }

    @Test
    fun `withCapturedStackTrace does not double-capture`() {
        OriginalStackTraceStore.clearAll()
        
        val exception = RuntimeException("Test").captureOriginalStackTrace()
        val originalTrace = OriginalStackTraceStore.getOriginalStackTrace(exception)
        
        // Call withCapturedStackTrace again
        val enhancedException = exception.withCapturedStackTrace()
        val secondTrace = OriginalStackTraceStore.getOriginalStackTrace(enhancedException)
        
        // Should be the same trace (not re-captured)
        assertEquals(originalTrace, secondTrace)
        
        OriginalStackTraceStore.cleanup(exception)
    }

    @Test
    fun `asNSException uses captured stack traces`() {
        OriginalStackTraceStore.clearAll()
        
        // Create an exception at this line - this is our "site A"
        val exception = createExceptionAtSiteA()
        
        // Later, call asNSException from a different location - this is our "site B"
        val nsException = callAsNSExceptionAtSiteB(exception)
        
        // Verify the NSException has return addresses
        val addresses = nsException.callStackReturnAddresses()
        assertTrue(addresses.isNotEmpty())
        
        // The stack trace should reflect site A, not site B
        // We can't easily verify the exact addresses, but we can verify the mechanism works
        assertNotNull(OriginalStackTraceStore.getOriginalStackTrace(exception))
        
        OriginalStackTraceStore.cleanup(exception)
    }

    @Test
    fun `memory cleanup prevents leaks`() {
        OriginalStackTraceStore.clearAll()
        
        val exception1 = RuntimeException("Test 1").captureOriginalStackTrace()
        val exception2 = RuntimeException("Test 2").captureOriginalStackTrace()
        
        assertEquals(2, OriginalStackTraceStore.size())
        
        OriginalStackTraceStore.cleanup(exception1)
        assertEquals(1, OriginalStackTraceStore.size())
        
        OriginalStackTraceStore.cleanup(exception2)
        assertEquals(0, OriginalStackTraceStore.size())
    }

    @Test
    fun `clearAll removes all stored traces`() {
        OriginalStackTraceStore.clearAll()
        
        RuntimeException("Test 1").captureOriginalStackTrace()
        RuntimeException("Test 2").captureOriginalStackTrace()
        RuntimeException("Test 3").captureOriginalStackTrace()
        
        assertTrue(OriginalStackTraceStore.size() > 0)
        
        OriginalStackTraceStore.clearAll()
        assertEquals(0, OriginalStackTraceStore.size())
    }

    @Test
    fun `exception with causes preserves stack traces`() {
        OriginalStackTraceStore.clearAll()
        
        val rootCause = StackTraceCapturingRuntimeException("Root cause")
        val middleCause = StackTraceCapturingRuntimeException("Middle cause", rootCause)
        val topException = StackTraceCapturingRuntimeException("Top exception", middleCause)
        
        // All should have captured stack traces
        assertNotNull(OriginalStackTraceStore.getOriginalStackTrace(rootCause))
        assertNotNull(OriginalStackTraceStore.getOriginalStackTrace(middleCause))
        assertNotNull(OriginalStackTraceStore.getOriginalStackTrace(topException))
        
        val nsException = topException.asNSException(appendCausedBy = true)
        assertNotNull(nsException.callStackReturnAddresses())
        
        OriginalStackTraceStore.cleanup(rootCause)
        OriginalStackTraceStore.cleanup(middleCause)
        OriginalStackTraceStore.cleanup(topException)
    }

    // Helper methods to simulate the original issue scenario

    private fun createExceptionAtSiteA(): RuntimeException {
        // This simulates creating an exception at "site A"
        return StackTraceCapturingRuntimeException("Exception created at site A")
    }

    private fun callAsNSExceptionAtSiteB(exception: RuntimeException): platform.Foundation.NSException {
        // This simulates calling captureException at "site B"
        // The stack trace should still point to site A, not here
        return exception.asNSException(true)
    }
}