package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.User
import io.sentry.kotlin.multiplatform.protocol.UserFeedback

internal expect object SentryBridge {

    fun init(context: Context, configuration: OptionsConfiguration)

    fun init(configuration: OptionsConfiguration)

    fun captureMessage(message: String): SentryId

    fun captureMessage(message: String, scopeCallback: ScopeCallback): SentryId

    fun captureException(throwable: Throwable): SentryId

    fun captureException(throwable: Throwable, scopeCallback: ScopeCallback): SentryId

    fun configureScope(scopeCallback: ScopeCallback)

    fun captureUserFeedback(userFeedback: UserFeedback)

    fun addBreadcrumb(breadcrumb: Breadcrumb)

    fun setUser(user: User?)

    fun startTransaction(name: String, operation: String): Span

    fun startTransaction(name: String, operation: String, bindToScope: Boolean): Span

    fun startTransaction(
        transactionContext: TransactionContext,
        customSamplingContext: Map<String, Any>?
    ): Span

    fun startTransaction(
        transactionContext: TransactionContext,
        customSamplingContext: Map<String, Any>?,
        bindToScope: Boolean
    ): Span

    fun getSpan(): Span?

    fun close()
}
