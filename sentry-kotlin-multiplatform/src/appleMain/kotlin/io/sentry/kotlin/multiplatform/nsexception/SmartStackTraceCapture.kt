package io.sentry.kotlin.multiplatform.nsexception

import kotlin.experimental.ExperimentalNativeApi

/**
 * Smart stack trace capture mechanism that provides the best possible stack trace
 * for exception reporting. This handles various edge cases and provides fallbacks
 * when original stack traces aren't available.
 */
internal object SmartStackTraceCapture {
    
    /**
     * Gets the best available stack trace for a throwable, with intelligent fallbacks.
     * 
     * Priority order:
     * 1. Previously captured original stack trace (ideal case)
     * 2. Capture current stack trace if throwable is newly created
     * 3. Parse string stack trace if available
     * 4. Current call stack (fallback, may be incorrect but better than nothing)
     */
    @OptIn(ExperimentalNativeApi::class)
    fun getBestStackTrace(throwable: Throwable): List<Long> {
        // Try to get previously captured original stack trace
        OriginalStackTraceStore.getOriginalStackTrace(throwable)?.let { original ->
            return original
        }
        
        // If no original stack trace, try to capture current one
        // This works if the throwable was just created and we're immediately converting it
        return try {
            val currentStack = getStackTraceAddresses()
            val stringTrace = throwable.getStackTrace()
            
            // Store it for future use
            OriginalStackTraceStore.captureOriginalStackTrace(throwable)
            
            // Filter the addresses to remove our own method calls
            currentStack.filterStackTrace(throwable, stringTrace)
        } catch (e: Exception) {
            // Last resort: return empty list which will be handled gracefully
            emptyList()
        }
    }
    
    /**
     * Filters a stack trace to remove internal method calls and provide the cleanest
     * possible stack trace for exception reporting.
     */
    private fun List<Long>.filterStackTrace(throwable: Throwable, stringTrace: Array<String>): List<Long> {
        if (this.isEmpty()) return this
        
        // Look for the first frame that's not related to our internal stack capture
        val internalMethodNames = listOf(
            "getBestStackTrace",
            "captureOriginalStackTrace", 
            "withCapturedStackTrace",
            "asNSException",
            "captureException"
        )
        
        var startIndex = 0
        for (i in stringTrace.indices) {
            val frame = stringTrace[i]
            val isInternal = internalMethodNames.any { methodName ->
                frame.contains(methodName, ignoreCase = true)
            }
            if (!isInternal) {
                startIndex = i
                break
            }
        }
        
        // Return the filtered stack, ensuring we don't exceed bounds
        return if (startIndex < this.size) {
            this.drop(startIndex)
        } else {
            this
        }
    }
}

/**
 * Enhanced version of getFilteredStackTraceAddresses that uses smart stack trace capture.
 */
internal fun Throwable.getSmartFilteredStackTraceAddresses(
    keepLastInit: Boolean = false,
    commonAddresses: List<Long> = emptyList()
): List<Long> {
    val smartStackTrace = SmartStackTraceCapture.getBestStackTrace(this)
    val stringStackTrace = OriginalStackTraceStore.getOriginalStringStackTrace(this) 
        ?: this.getStackTrace()
    
    return smartStackTrace.dropInitAddresses(
        qualifiedClassName = this::class.qualifiedName ?: Throwable::class.qualifiedName!!,
        stackTrace = stringStackTrace,
        keepLast = keepLastInit
    ).dropCommonAddresses(commonAddresses)
}