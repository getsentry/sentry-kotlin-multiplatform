package io.sentry.kotlin.multiplatform.nsexception

import kotlin.native.identityHashCode
import kotlin.native.concurrent.ThreadLocal
import kotlin.experimental.ExperimentalNativeApi

/**
 * A thread-safe storage mechanism for preserving original stack traces of exceptions.
 * This allows us to capture stack traces at exception creation time and retrieve them
 * later when converting to NSException, solving the issue where getStackTraceAddresses()
 * captures the current call stack instead of the original exception stack.
 */
@ThreadLocal
internal object OriginalStackTraceStore {
    
    // Use identity hash codes as keys to avoid memory leaks
    private val stackTraceStorage = mutableMapOf<Int, List<Long>>()
    private val stringStackTraceStorage = mutableMapOf<Int, Array<String>>()
    
    /**
     * Stores the original stack trace for a throwable at creation time.
     * This should be called as early as possible in the exception lifecycle.
     */
    @OptIn(ExperimentalNativeApi::class)
    fun captureOriginalStackTrace(throwable: Throwable) {
        val key = throwable.identityHashCode()
        
        try {
            // Capture current stack addresses - this represents the original exception location
            val addresses = getStackTraceAddresses()
            val stringTrace = throwable.getStackTrace()
            
            // Store both representations for flexibility
            stackTraceStorage[key] = addresses
            stringStackTraceStorage[key] = stringTrace
            
        } catch (e: Exception) {
            // Fallback: if native stack capture fails, store empty list
            // This ensures we don't break exception creation
            stackTraceStorage[key] = emptyList()
            stringStackTraceStorage[key] = emptyArray()
        }
    }
    
    /**
     * Retrieves the original stack trace addresses for a throwable.
     * Returns null if no original stack trace was captured.
     */
    fun getOriginalStackTrace(throwable: Throwable): List<Long>? {
        val key = throwable.identityHashCode()
        return stackTraceStorage[key]
    }
    
    /**
     * Retrieves the original string stack trace for a throwable.
     * Returns null if no original stack trace was captured.
     */
    fun getOriginalStringStackTrace(throwable: Throwable): Array<String>? {
        val key = throwable.identityHashCode()
        return stringStackTraceStorage[key]
    }
    
    /**
     * Removes stored stack traces for a throwable to prevent memory leaks.
     * Should be called after the exception has been processed.
     */
    fun cleanup(throwable: Throwable) {
        val key = throwable.identityHashCode()
        stackTraceStorage.remove(key)
        stringStackTraceStorage.remove(key)
    }
    
    /**
     * Clears all stored stack traces. Useful for testing or memory management.
     */
    fun clearAll() {
        stackTraceStorage.clear()
        stringStackTraceStorage.clear()
    }
    
    /**
     * Returns the number of stored stack traces (for debugging/monitoring).
     */
    fun size(): Int = stackTraceStorage.size
}