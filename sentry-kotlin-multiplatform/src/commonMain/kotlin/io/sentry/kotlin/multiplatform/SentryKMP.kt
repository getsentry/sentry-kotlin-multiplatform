package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.User
import io.sentry.kotlin.multiplatform.protocol.UserFeedback
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

public typealias ScopeCallback = (Scope) -> Unit
public typealias OptionsConfiguration = (SentryOptions) -> Unit

public expect abstract class Context

/** Sentry Kotlin Multiplatform SDK API entry point */
public object Sentry {

    /**
     * Sentry initialization with an option configuration handler.
     *
     * @param context: The context (used for retrieving Android Context)
     * @param configuration Options configuration handler.
     */
    @OptIn(ExperimentalObjCRefinement::class)
    @HiddenFromObjC
    public fun init(context: Context, configuration: OptionsConfiguration) {
        SentryBridge.init(context, configuration)
    }

    /**
     * Sentry initialization with an option configuration handler.
     * This is a convenience init for direct initialization on Apple platforms.
     *
     * @param configuration Options configuration handler.
     */
    public fun init(configuration: OptionsConfiguration) {
        SentryBridge.init(configuration = configuration)
    }

    /**
     * Captures the message.
     *
     * @param message The message to send.
     */
    public fun captureMessage(message: String): SentryId {
        return SentryBridge.captureMessage(message)
    }

    /**
     * Captures the exception.
     *
     * @param message The message to send.
     * @param scopeCallback The local scope callback.
     */
    public fun captureMessage(message: String, scopeCallback: ScopeCallback): SentryId {
        return SentryBridge.captureMessage(message, scopeCallback)
    }

    /**
     * Captures the exception.
     *
     * @param throwable The exception.
     */
    public fun captureException(throwable: Throwable): SentryId {
        return SentryBridge.captureException(throwable)
    }

    /**
     * Captures the exception.
     *
     * @param throwable The exception.
     * @param scopeCallback The local scope callback.
     */
    public fun captureException(throwable: Throwable, scopeCallback: ScopeCallback): SentryId {
        return SentryBridge.captureException(throwable, scopeCallback)
    }

    /**
     * Captures a manually created user feedback and sends it to Sentry.
     *
     * @param userFeedback The user feedback to send to Sentry.
     */
    public fun captureUserFeedback(userFeedback: UserFeedback) {
        return SentryBridge.captureUserFeedback(userFeedback)
    }

    /**
     * Configures the scope through the callback.
     *
     * @param scopeCallback The configure scope callback.
     */
    public fun configureScope(scopeCallback: ScopeCallback) {
        SentryBridge.configureScope(scopeCallback)
    }

    /**
     * Adds a breadcrumb to the current Scope.
     *
     * @param breadcrumb The breadcrumb to add.
     */
    public fun addBreadcrumb(breadcrumb: Breadcrumb) {
        SentryBridge.addBreadcrumb(breadcrumb)
    }

    /**
     * Sets the user to the current scope.
     *
     * @param user The user to set.
     */
    public fun setUser(user: User?) {
        SentryBridge.setUser(user)
    }

    public fun startTransaction(name: String, operation: String): Span {
        return SentryBridge.startTransaction(name, operation)
    }

    /**
     * Throws a RuntimeException, useful for testing.
     */
    public fun crash() {
        throw RuntimeException("Uncaught Exception from Kotlin Multiplatform.")
    }

    /**
     * Closes the SDK.
     */
    public fun close() {
        SentryBridge.close()
    }
}

public data class SpanContext(val span: Span) : AbstractCoroutineContextElement(SpanContext) {
    public companion object Key : CoroutineContext.Key<SpanContext>
}

public fun CoroutineContext.currentSpan(): Span {
    return get(SpanContext)?.span ?: throw IllegalStateException("No active Span in context")
}

public suspend fun <T> withTrace(
    name: String,
    spanName: String,
    block: suspend CoroutineScope.() -> T
): T {
    val trace = Sentry.startTransaction(name, spanName)
    return withSpan(trace, block)
}

public suspend fun <T> withSpan(name: String, block: suspend CoroutineScope.() -> T): T {
    val span = currentCoroutineContext().currentSpan()
    val childSpan = span.startChild(name)
    return withSpan(childSpan, block)
}

public suspend fun <T> withSpan(span: Span, block: suspend CoroutineScope.() -> T): T {
    return try {
        withContext(SpanContext(span), block)
    } catch (e: Throwable) {
        if (e is CancellationException) {
            span.status = SpanStatus.CANCELLED
        } else {
            span.status = SpanStatus.INTERNAL_ERROR
        }
        throw e
    } finally {
        span.finish()
    }
}