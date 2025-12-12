package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.User
import io.sentry.kotlin.multiplatform.protocol.UserFeedback
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

public typealias ScopeCallback = (Scope) -> Unit
public typealias OptionsConfiguration = (SentryOptions) -> Unit
public typealias PlatformOptionsConfiguration = (SentryPlatformOptions) -> Unit

/** The context used for Android initiaclization. */
@Deprecated("No longer necessary to initialize Sentry on Android.")
public expect abstract class Context

/** Sentry Kotlin Multiplatform SDK API entry point. */
public object Sentry {
    private val bridge = SentryBridge()

    /**
     * Sentry initialization with an option configuration handler.
     *
     * @param context: The context (used for retrieving Android Context)
     * @param configuration Options configuration handler.
     */
    @OptIn(ExperimentalObjCRefinement::class)
    @HiddenFromObjC
    @Deprecated(
        "Use init(OptionsConfiguration) instead.",
        ReplaceWith("Sentry.init(configuration)")
    )
    public fun init(context: Context, configuration: OptionsConfiguration) {
        bridge.init(context, configuration)
    }

    /**
     * Sentry initialization with an option configuration handler.
     * This is a convenience init for direct initialization on Apple platforms.
     *
     * @param configuration Options configuration handler.
     */
    public fun init(configuration: OptionsConfiguration) {
        bridge.init(configuration = configuration)
    }

    /**
     * Sentry initialization with an option configuration handler for platform options.
     *
     * Since [SentryPlatformOptions] is implemented via typealias you need to have direct access
     * to the platform-specific SDKs API to use this method.
     *
     * ### Java / Android
     *   You can achieve this by specifying `compileOnly(java/android_dependency)`
     *   in your `build.gradle.kts`.
     *
     * ### Cocoa
     * - This integration requires the use of the Cocoapods gradle plugin and
     *   adding Sentry as a pod
     *
     * @see SentryPlatformOptions
     * @param configuration Platform options configuration handler.
     */
    public fun initWithPlatformOptions(configuration: PlatformOptionsConfiguration) {
        bridge.initWithPlatformOptions(configuration)
    }

    /**
     * Captures the message.
     *
     * @param message The message to send.
     */
    public fun captureMessage(message: String): SentryId {
        return bridge.captureMessage(message)
    }

    /**
     * Captures the exception.
     *
     * @param message The message to send.
     * @param scopeCallback The local scope callback.
     */
    public fun captureMessage(message: String, scopeCallback: ScopeCallback): SentryId {
        return bridge.captureMessage(message, scopeCallback)
    }

    /**
     * Captures the exception.
     *
     * @param throwable The exception.
     */
    public fun captureException(throwable: Throwable): SentryId {
        return bridge.captureException(throwable)
    }

    /**
     * Captures the exception.
     *
     * @param throwable The exception.
     * @param scopeCallback The local scope callback.
     */
    public fun captureException(throwable: Throwable, scopeCallback: ScopeCallback): SentryId {
        return bridge.captureException(throwable, scopeCallback)
    }

    /**
     * Captures a manually created user feedback and sends it to Sentry.
     *
     * @param userFeedback The user feedback to send to Sentry.
     */
    public fun captureUserFeedback(userFeedback: UserFeedback) {
        return bridge.captureUserFeedback(userFeedback)
    }

    /**
     * Configures the scope through the callback.
     *
     * @param scopeCallback The configure scope callback.
     */
    public fun configureScope(scopeCallback: ScopeCallback) {
        bridge.configureScope(scopeCallback)
    }

    /**
     * Adds a breadcrumb to the current Scope.
     *
     * @param breadcrumb The breadcrumb to add.
     */
    public fun addBreadcrumb(breadcrumb: Breadcrumb) {
        bridge.addBreadcrumb(breadcrumb)
    }

    /**
     * Sets the user to the current scope.
     *
     * @param user The user to set.
     */
    public fun setUser(user: User?) {
        bridge.setUser(user)
    }

    /**
     * Returns the Sentry logger API for sending structured logs.
     *
     * On JVM, this returns the Java SDK's ILoggerApi directly (via typealias),
     * meaning calls go directly to the Java implementation with zero wrapper overhead.
     *
     * Usage:
     * ```
     * Sentry.logger().info("A simple log message")
     * Sentry.logger().error("A %s log message", "formatted")
     * Sentry.logger().log(SentryLogLevel.DEBUG, "Log at specific level")
     * ```
     *
     * @return The logger API for sending structured logs
     */
    public fun logger(): SentryLoggerApi {
        return loggerFactory()
    }

    /**
     * Returns true if the app crashed during last run.
     */
    public fun isCrashedLastRun(): Boolean {
        return bridge.isCrashedLastRun()
    }

    /**
     * Throws a RuntimeException, useful for testing.
     */
    public fun crash() {
        throw RuntimeException("Uncaught Exception from Kotlin Multiplatform.")
    }

    /**
     * Checks if the SDK is enabled.
     */
    public fun isEnabled(): Boolean {
        return bridge.isEnabled()
    }

    /**
     * Closes the SDK.
     */
    public fun close() {
        bridge.close()
    }
}
