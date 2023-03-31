package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.User
import io.sentry.kotlin.multiplatform.protocol.UserFeedback
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
