package io.sentry.kotlin.multiplatform

import io.sentry.CustomSamplingContext
import io.sentry.Sentry
import io.sentry.TransactionOptions
import io.sentry.kotlin.multiplatform.converters.toJvm
import io.sentry.kotlin.multiplatform.extensions.toJvmBreadcrumb
import io.sentry.kotlin.multiplatform.extensions.toJvmUser
import io.sentry.kotlin.multiplatform.extensions.toJvmUserFeedback
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.User
import io.sentry.kotlin.multiplatform.protocol.UserFeedback
import io.sentry.kotlin.multiplatform.CustomSamplingContext as KmpCustomSamplingContext

internal expect fun initSentry(configuration: OptionsConfiguration)

internal actual object SentryBridge {
    actual fun init(context: Context, configuration: OptionsConfiguration) {
        initSentry(configuration)
    }

    actual fun init(configuration: OptionsConfiguration) {
        initSentry(configuration = configuration)
    }

    actual fun captureMessage(message: String): SentryId {
        val jvmSentryId = Sentry.captureMessage(message)
        return SentryId(jvmSentryId.toString())
    }

    actual fun captureMessage(message: String, scopeCallback: ScopeCallback): SentryId {
        val jvmSentryId = Sentry.captureMessage(message, configureScopeCallback(scopeCallback))
        return SentryId(jvmSentryId.toString())
    }

    actual fun captureException(throwable: Throwable): SentryId {
        val jvmSentryId = Sentry.captureException(throwable)
        return SentryId(jvmSentryId.toString())
    }

    actual fun captureException(throwable: Throwable, scopeCallback: ScopeCallback): SentryId {
        val jvmSentryId = Sentry.captureException(throwable, configureScopeCallback(scopeCallback))
        return SentryId(jvmSentryId.toString())
    }

    actual fun captureUserFeedback(userFeedback: UserFeedback) {
        Sentry.captureUserFeedback(userFeedback.toJvmUserFeedback())
    }

    actual fun configureScope(scopeCallback: ScopeCallback) {
        Sentry.configureScope(configureScopeCallback(scopeCallback))
    }

    actual fun addBreadcrumb(breadcrumb: Breadcrumb) {
        Sentry.addBreadcrumb(breadcrumb.toJvmBreadcrumb())
    }

    actual fun setUser(user: User?) {
        Sentry.setUser(user?.toJvmUser())
    }

    actual fun startTransaction(name: String, operation: String): Span {
        val jvmTransaction = Sentry.startTransaction(name, operation)
        return SpanAdapter(jvmTransaction)
    }

    actual fun startTransaction(name: String, operation: String, bindToScope: Boolean): Span {
        val jvmTransaction = Sentry.startTransaction(
            name,
            operation,
            TransactionOptions().apply { this.isBindToScope = bindToScope }
        )
        return SpanAdapter(jvmTransaction)
    }

    actual fun startTransaction(
        transactionContext: TransactionContext,
        customSamplingContext: KmpCustomSamplingContext
    ): Span {
        val jvmCustomSamplingContext = customSamplingContext?.toJvm() ?: CustomSamplingContext()
        val jvmTransactionContext = transactionContext.toJvm()
        val jvmTransaction =
            Sentry.startTransaction(
                jvmTransactionContext,
                TransactionOptions().apply {
                    this.customSamplingContext = jvmCustomSamplingContext
                }
            )
        return SpanAdapter(jvmTransaction)
    }

    actual fun startTransaction(
        transactionContext: TransactionContext,
        customSamplingContext: KmpCustomSamplingContext,
        bindToScope: Boolean
    ): Span {
        val jvmCustomSamplingContext = customSamplingContext?.toJvm()
        val jvmTransactionContext = transactionContext.toJvm()
        val jvmTransaction =
            Sentry.startTransaction(
                jvmTransactionContext,
                TransactionOptions().apply {
                    this.isBindToScope = bindToScope
                    this.customSamplingContext = jvmCustomSamplingContext
                }
            )
        return SpanAdapter(jvmTransaction)
    }

    actual fun getSpan(): Span? {
        val jvmSpan = Sentry.getSpan()
        return jvmSpan?.let { SpanAdapter(it) }
    }

    actual fun close() {
        Sentry.close()
    }

    private fun configureScopeCallback(scopeCallback: ScopeCallback): (JvmIScope) -> Unit {
        return {
            val jvmScopeProvider = ScopeAdapter(it)
            scopeCallback.invoke(jvmScopeProvider)
        }
    }
}
