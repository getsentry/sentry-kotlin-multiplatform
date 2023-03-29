package io.sentry.kotlin.multiplatform

import io.sentry.Sentry
import io.sentry.kotlin.multiplatform.extensions.toJvmBreadcrumb
import io.sentry.kotlin.multiplatform.extensions.toJvmUser
import io.sentry.kotlin.multiplatform.extensions.toJvmUserFeedback
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.User
import io.sentry.kotlin.multiplatform.protocol.UserFeedback

internal expect fun initSentry(context: Context? = null, configuration: OptionsConfiguration)

internal actual object SentryBridge {

    actual fun init(context: Context, configuration: OptionsConfiguration) {
        initSentry(context, configuration)
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

    actual fun close() {
        Sentry.close()
    }

    private fun configureScopeCallback(scopeCallback: ScopeCallback): (JvmScope) -> Unit {
        return {
            val jvmScopeProvider = JvmScopeProvider(it)
            val scope = Scope(jvmScopeProvider)
            scopeCallback.invoke(scope)
        }
    }
}
