package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentrySDK
import cocoapods.Sentry.SentrySpanProtocol
import io.sentry.kotlin.multiplatform.extensions.toCocoa
import io.sentry.kotlin.multiplatform.extensions.toCocoaBreadcrumb
import io.sentry.kotlin.multiplatform.extensions.toCocoaUser
import io.sentry.kotlin.multiplatform.extensions.toCocoaUserFeedback
import io.sentry.kotlin.multiplatform.nsexception.asNSException
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.User
import io.sentry.kotlin.multiplatform.protocol.UserFeedback
import platform.Foundation.NSError
import platform.Foundation.NSException

public actual abstract class Context

internal expect fun initSentry(configuration: OptionsConfiguration)

internal actual object SentryBridge {

    actual fun init(context: Context, configuration: OptionsConfiguration) {
        initSentry(configuration)
    }

    actual fun init(configuration: OptionsConfiguration) {
        initSentry(configuration)
    }

    actual fun captureMessage(message: String): SentryId {
        val cocoaSentryId = SentrySDK.captureMessage(message)
        return SentryId(cocoaSentryId.toString())
    }

    actual fun captureMessage(message: String, scopeCallback: ScopeCallback): SentryId {
        val cocoaSentryId = SentrySDK.captureMessage(message, configureScopeCallback(scopeCallback))
        return SentryId(cocoaSentryId.toString())
    }

    actual fun captureException(throwable: Throwable): SentryId {
        val cocoaSentryId = SentrySDK.captureException(throwable.asNSException(true))
        return SentryId(cocoaSentryId.toString())
    }

    actual fun captureException(throwable: Throwable, scopeCallback: ScopeCallback): SentryId {
        val cocoaSentryId = SentrySDK.captureException(
            throwable.asNSException(true),
            configureScopeCallback(scopeCallback)
        )
        return SentryId(cocoaSentryId.toString())
    }

    actual fun captureUserFeedback(userFeedback: UserFeedback) {
        SentrySDK.captureUserFeedback(userFeedback.toCocoaUserFeedback())
    }

    actual fun configureScope(scopeCallback: ScopeCallback) {
        SentrySDK.configureScope(configureScopeCallback(scopeCallback))
    }

    actual fun addBreadcrumb(breadcrumb: Breadcrumb) {
        SentrySDK.addBreadcrumb(breadcrumb.toCocoaBreadcrumb())
    }

    actual fun setUser(user: User?) {
        SentrySDK.setUser(user?.toCocoaUser())
    }

    actual fun startTransaction(name: String, operation: String): Span {
        val cocoaSpan = SentrySDK.startTransactionWithName(name, operation)
        return CocoaSpanProvider(cocoaSpan)
    }

    actual fun startTransaction(name: String, operation: String, bindToScope: Boolean): Span {
        val cocoaSpan = SentrySDK.startTransactionWithName(name, operation, bindToScope)
        return CocoaSpanProvider(cocoaSpan)
    }

    actual fun getSpan(): Span? {
        val cocoaSpan = SentrySDK.span
        return cocoaSpan?.let { CocoaSpanProvider(it) }
    }

    actual fun close() {
        SentrySDK.close()
    }

    private fun configureScopeCallback(scopeCallback: ScopeCallback): (CocoaScope?) -> Unit {
        return { cocoaScope ->
            val cocoaScopeProvider = cocoaScope?.let {
                CocoaScopeProvider(it)
            }
            cocoaScopeProvider?.let {
                scopeCallback.invoke(it)
            }
        }
    }
}

@Suppress("unused")
public fun Sentry.captureError(error: NSError) {
    SentrySDK.captureError(error)
}

@Suppress("unused")
public fun Sentry.captureException(exception: NSException) {
    SentrySDK.captureException(exception)
}
