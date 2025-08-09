package io.sentry.kotlin.multiplatform.nsexception

import kotlin.experimental.ExperimentalNativeApi

/**
 * Extension function to enable any Throwable to capture its original stack trace.
 * This should be called immediately after exception creation to preserve the original stack.
 */
@OptIn(ExperimentalNativeApi::class)
fun Throwable.captureOriginalStackTrace(): Throwable {
    OriginalStackTraceStore.captureOriginalStackTrace(this)
    return this
}

/**
 * A RuntimeException that automatically captures its stack trace at creation time.
 * This ensures the original stack trace is preserved for Sentry reporting on Apple platforms.
 */
open class StackTraceCapturingRuntimeException : RuntimeException {
    
    constructor() : super() {
        captureOriginalStackTrace()
    }
    
    constructor(message: String?) : super(message) {
        captureOriginalStackTrace()
    }
    
    constructor(message: String?, cause: Throwable?) : super(message, cause) {
        captureOriginalStackTrace()
    }
    
    constructor(cause: Throwable?) : super(cause) {
        captureOriginalStackTrace()
    }
}

/**
 * An IllegalArgumentException that automatically captures its stack trace at creation time.
 */
class StackTraceCapturingIllegalArgumentException : StackTraceCapturingRuntimeException {
    
    constructor() : super()
    
    constructor(message: String?) : super(message)
    
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    
    constructor(cause: Throwable?) : super(cause)
}

/**
 * An IllegalStateException that automatically captures its stack trace at creation time.
 */
class StackTraceCapturingIllegalStateException : StackTraceCapturingRuntimeException {
    
    constructor() : super()
    
    constructor(message: String?) : super(message)
    
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    
    constructor(cause: Throwable?) : super(cause)
}

/**
 * A generic Exception that automatically captures its stack trace at creation time.
 */
open class StackTraceCapturingException : Exception {
    
    constructor() : super() {
        captureOriginalStackTrace()
    }
    
    constructor(message: String?) : super(message) {
        captureOriginalStackTrace()
    }
    
    constructor(message: String?, cause: Throwable?) : super(message, cause) {
        captureOriginalStackTrace()
    }
    
    constructor(cause: Throwable?) : super(cause) {
        captureOriginalStackTrace()
    }
}

/**
 * Utility function to wrap any existing throwable with stack trace capturing capability.
 * This is useful when you receive a throwable from external code and want to ensure
 * its original stack trace is preserved for Sentry reporting.
 */
fun Throwable.withCapturedStackTrace(): Throwable {
    // If this throwable already has a captured stack trace, return as-is
    if (OriginalStackTraceStore.getOriginalStackTrace(this) != null) {
        return this
    }
    
    // Otherwise, capture the current stack trace and return the same instance
    return this.captureOriginalStackTrace()
}