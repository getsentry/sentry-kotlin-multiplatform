package io.sentry.kotlin.multiplatform

import NSException.Sentry.SentryEvent
import PrivateSentrySDKOnly.Sentry.PrivateSentrySDKOnly
import cocoapods.Sentry.SentryFrame
import cocoapods.Sentry.SentrySDK
import cocoapods.Sentry.SentryThread
import io.sentry.kotlin.multiplatform.extensions.toCocoaBreadcrumb
import io.sentry.kotlin.multiplatform.extensions.toCocoaUser
import io.sentry.kotlin.multiplatform.extensions.toCocoaUserFeedback
import io.sentry.kotlin.multiplatform.nsexception.asNSException
import io.sentry.kotlin.multiplatform.nsexception.dropKotlinCrashEvent
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.User
import io.sentry.kotlin.multiplatform.protocol.UserFeedback
import io.sentry.kotlin.multiplatform.SentryStackTraceTrimmer.removeSentryFrames
import platform.Foundation.NSError
import platform.Foundation.NSException

public actual abstract class Context

// Since the function is the same on all apple platforms, we don't split it into expect/actual
// like on JVM and Android, we may do that later on if needed.
internal actual fun SentryPlatformOptions.prepareForInit() {
    val cocoa = this as? CocoaSentryOptions
    val existingBeforeSend = cocoa?.beforeSend
    val modifiedBeforeSend: (CocoaSentryEvent?) -> CocoaSentryEvent? = beforeSend@{ event ->
        // Return early if the user's beforeSend returns null
        if (existingBeforeSend?.invoke(event) == null) {
            return@beforeSend null
        }

        val threads = event?.threads as? List<SentryThread>
        threads?.forEach { thread ->
            thread.stacktrace?.let { stackTrace ->
                removeSentryFrames(stackTrace)
            }
        }

        val cocoaName = BuildKonfig.SENTRY_COCOA_PACKAGE_NAME
        val cocoaVersion = BuildKonfig.SENTRY_COCOA_VERSION

        val sdk = event?.sdk?.toMutableMap() ?: mutableMapOf()
        val packages = sdk["packages"] as? MutableList<Map<String, String>> ?: mutableListOf()

        packages.add(mapOf("name" to cocoaName, "version" to cocoaVersion))
        sdk["packages"] = packages
        event?.sdk = sdk

        dropKotlinCrashEvent(event as SentryEvent?) as CocoaSentryEvent?
    }

    cocoa?.setBeforeSend(modifiedBeforeSend)

    PrivateSentrySDKOnly.setSdkName(BuildKonfig.SENTRY_KMP_COCOA_SDK_NAME, BuildKonfig.VERSION_NAME)
}

internal actual class SentryBridge actual constructor(private val sentryInstance: SentryInstance) {
    actual fun init(context: Context, configuration: OptionsConfiguration) {
        init(configuration)
    }

    actual fun init(configuration: OptionsConfiguration) {
        val options = SentryOptions()
        configuration.invoke(options)
        initWithPlatformOptions(options.toPlatformOptionsConfiguration())
    }

    actual fun initWithPlatformOptions(configuration: PlatformOptionsConfiguration) {
        val finalConfiguration: PlatformOptionsConfiguration = {
            configuration(it)
            // We modify beforeSend so we need this to run after the user's configuration
            it.prepareForInit()
        }
        sentryInstance.init(finalConfiguration)
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

    actual fun isCrashedLastRun(): Boolean {
        return SentrySDK.crashedLastRun()
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
