package io.sentry.kotlin.multiplatform

import Internal.Sentry.kSentryLevelError
import cocoapods.Sentry.PrivateSentrySDKOnly
import cocoapods.Sentry.SentrySDK
import cocoapods.Sentry.addBreadcrumb
import cocoapods.Sentry.captureError
import cocoapods.Sentry.captureEvent
import cocoapods.Sentry.captureException
import cocoapods.Sentry.captureFeedback
import cocoapods.Sentry.captureMessage
import cocoapods.Sentry.close
import cocoapods.Sentry.configureScope
import cocoapods.Sentry.crashedLastRun
import cocoapods.Sentry.isEnabled
import cocoapods.Sentry.logger
import cocoapods.Sentry.setUser
import cocoapods.Sentry.startOption
import io.sentry.kotlin.multiplatform.extensions.toCocoaBreadcrumb
import io.sentry.kotlin.multiplatform.extensions.toCocoaUser
import io.sentry.kotlin.multiplatform.extensions.toCocoaUserFeedback
import io.sentry.kotlin.multiplatform.log.CocoaSentryLoggerAdapter
import io.sentry.kotlin.multiplatform.log.SentryLogger
import io.sentry.kotlin.multiplatform.nsexception.asSentryEvent
import io.sentry.kotlin.multiplatform.nsexception.dropKotlinCrashEvent
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.User
import io.sentry.kotlin.multiplatform.protocol.UserFeedback
import platform.Foundation.NSError
import platform.Foundation.NSException

public actual abstract class Context

// Since the function is the same on all apple platforms, we don't split it into expect/actual
// like on JVM and Android, we may do that later on if needed.
internal actual fun SentryPlatformOptions.prepareForInit() {
    val cocoa = this as? CocoaSentryOptions
    val userDefinedBeforeSend = cocoa?.beforeSend()
    val modifiedBeforeSend: (CocoaSentryEvent?) -> CocoaSentryEvent? = beforeSend@{ event ->
        // A replacement event could erase the marker on a duplicate Kotlin termination report.
        // Suppress that report before invoking the user's callback.
        if (dropKotlinCrashEvent(event) == null) return@beforeSend null
        val processedEvent =
            if (userDefinedBeforeSend != null) {
                userDefinedBeforeSend.invoke(event) ?: return@beforeSend null
            } else {
                event
            }
        val cocoaName = BuildKonfig.SENTRY_COCOA_PACKAGE_NAME
        val cocoaVersion = BuildKonfig.SENTRY_COCOA_VERSION

        val sdk = processedEvent?.sdk?.toMutableMap() ?: mutableMapOf()
        val packages = sdk["packages"] as? MutableList<Map<String, String>> ?: mutableListOf()

        packages.add(mapOf("name" to cocoaName, "version" to cocoaVersion))
        sdk["packages"] = packages
        processedEvent?.sdk = sdk

        dropKotlinCrashEvent(processedEvent)
    }

    cocoa?.setBeforeSend(modifiedBeforeSend)

    PrivateSentrySDKOnly.setSdkName(BuildKonfig.SENTRY_KMP_COCOA_SDK_NAME, BuildKonfig.VERSION_NAME)
}

internal actual class SentryBridge actual constructor(
    private val sentryInstance: SentryInstance,
) {
    private val logger =
        CocoaSentryLoggerAdapter({
            // Cocoa may invoke beforeSendLog before dropping disabled logs. Keep KMP's disabled
            // logger a no-op, including callbacks, and read the current options after SDK restart.
            if (SentrySDK.isEnabled() && SentrySDK.startOption()?.enableLogs() == true) SentrySDK.logger() else null
        })

    actual fun init(
        context: Context,
        configuration: OptionsConfiguration,
    ) {
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

    actual fun captureMessage(
        message: String,
        scopeCallback: ScopeCallback,
    ): SentryId {
        val cocoaSentryId = SentrySDK.captureMessage(message, configureScopeCallback(scopeCallback))
        return SentryId(cocoaSentryId.toString())
    }

    actual fun captureException(throwable: Throwable): SentryId {
        val event =
            throwable.asSentryEvent(
                level = kSentryLevelError,
                isHandled = true,
                markThreadAsCrashed = false,
            )
        val cocoaSentryId = SentrySDK.captureEvent(event)
        return SentryId(cocoaSentryId.toString())
    }

    actual fun captureException(
        throwable: Throwable,
        scopeCallback: ScopeCallback,
    ): SentryId {
        val event =
            throwable.asSentryEvent(
                level = kSentryLevelError,
                isHandled = true,
                markThreadAsCrashed = false,
            )
        val cocoaSentryId = SentrySDK.captureEvent(event, configureScopeCallback(scopeCallback))
        return SentryId(cocoaSentryId.toString())
    }

    actual fun captureUserFeedback(userFeedback: UserFeedback) {
        SentrySDK.captureFeedback(userFeedback.toCocoaUserFeedback())
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

    actual fun isCrashedLastRun(): Boolean = SentrySDK.crashedLastRun()

    actual fun isEnabled(): Boolean = SentrySDK.isEnabled()

    actual fun close() {
        SentrySDK.close()
    }

    private fun configureScopeCallback(scopeCallback: ScopeCallback): (CocoaScope?) -> Unit =
        { cocoaScope ->
            val cocoaScopeProvider =
                cocoaScope?.let {
                    CocoaScopeProvider(it)
                }
            cocoaScopeProvider?.let {
                scopeCallback.invoke(it)
            }
        }

    actual fun logger(): SentryLogger = logger
}

@Suppress("unused")
public fun Sentry.captureError(error: NSError) {
    SentrySDK.captureError(error)
}

@Suppress("unused")
public fun Sentry.captureException(exception: NSException) {
    SentrySDK.captureException(exception)
}
