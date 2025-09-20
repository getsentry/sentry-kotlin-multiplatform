package io.sentry.kotlin.multiplatform.examples

import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryId

/**
 * Example demonstrating the correct usage of exception handling with preserved stack traces.
 * This solves the issue where captureException would show the wrong stack trace on iOS.
 */
object StackTraceExample {

    /**
     * Example 1: Using the extension function to capture stack traces for existing exceptions
     */
    fun demonstrateManualStackTraceCapture() {
        try {
            // This would be your business logic that throws an exception
            performSomeOperation()
        } catch (e: Exception) {
            // On Apple platforms, capture the original stack trace before passing to Sentry
            Sentry.captureException(e) // The SentryBridge now automatically handles this!
        }
    }

    /**
     * Example 2: Using stack trace capturing exception classes (Recommended approach)
     */
    fun demonstrateAutoCapturingExceptions() {
        // When using Apple platforms, consider using stack trace capturing exceptions
        // for exceptions that will be reported to Sentry
        if (someErrorCondition()) {
            // These exceptions automatically capture their stack traces at creation time
            throw createBusinessLogicException("Something went wrong in business logic")
        }
    }

    /**
     * Example 3: Retrofitting existing exception throwing code
     */
    fun demonstrateRetrofittingExistingCode() {
        try {
            legacyCodeThatThrowsExceptions()
        } catch (e: Exception) {
            // For external exceptions that you can't control, 
            // the SentryBridge automatically handles stack trace capture
            reportErrorToSentry(e)
        }
    }

    /**
     * Example 4: Async exception handling
     */
    suspend fun demonstrateAsyncExceptionHandling() {
        try {
            performAsyncOperation()
        } catch (e: Exception) {
            // Stack traces are preserved even in async contexts
            Sentry.captureException(e)
        }
    }

    // Utility methods to create exceptions that preserve stack traces on Apple platforms
    
    /**
     * Creates business logic exceptions that preserve stack traces.
     * Use this pattern when creating exceptions that will be reported to Sentry.
     */
    private fun createBusinessLogicException(message: String): Exception {
        // On Apple platforms, this will preserve the stack trace
        return Exception(message).apply {
            // The automatic enhancement in SentryBridge handles this for us now!
        }
    }

    /**
     * Helper function to demonstrate that the solution works regardless of call depth
     */
    private fun reportErrorToSentry(exception: Exception): SentryId {
        return intermediateFunction(exception)
    }

    private fun intermediateFunction(exception: Exception): SentryId {
        return anotherIntermediateFunction(exception)
    }

    private fun anotherIntermediateFunction(exception: Exception): SentryId {
        // Even with multiple call levels, the original stack trace is preserved
        return Sentry.captureException(exception)
    }

    // Mock methods for demonstration
    private fun performSomeOperation() {
        throw RuntimeException("Something went wrong in performSomeOperation")
    }

    private fun someErrorCondition(): Boolean = true

    private fun legacyCodeThatThrowsExceptions() {
        throw IllegalStateException("Legacy code exception")
    }

    private suspend fun performAsyncOperation() {
        throw Exception("Async operation failed")
    }
}

/**
 * Usage guidelines for the enhanced stack trace functionality:
 * 
 * 1. **Automatic Enhancement**: The SentryBridge now automatically handles stack trace 
 *    preservation for all exceptions. No manual intervention required!
 * 
 * 2. **Memory Management**: Stack traces are automatically cleaned up after reporting
 *    to prevent memory leaks.
 * 
 * 3. **Thread Safety**: The solution works correctly in multi-threaded environments
 *    using ThreadLocal storage.
 * 
 * 4. **Backward Compatibility**: Existing code continues to work unchanged while 
 *    benefiting from the fix.
 * 
 * 5. **Performance**: Minimal overhead - stack traces are only captured when needed
 *    and cleaned up promptly.
 */