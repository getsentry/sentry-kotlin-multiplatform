package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.SdkVersion

internal const val DEFAULT_MAX_BREADCRUMBS = 100
internal const val DEFAULT_MAX_ATTACHMENT_SIZE = 20 * 1024 * 1024L
internal const val DEFAULT_SESSION_INTERVAL_MILLIS = 30000L
internal const val DEFAULT_APPHANG_TIMEOUT_INTERVAL_MILLIS = 2000L
internal const val DEFAULT_ANR_TIMEOUT_INTERVAL_MILLIS = 5000L

/** Sentry options that can be used to configure the SDK. */
public open class SentryOptions {
    /**
     * The DSN tells the SDK where to send the events to. If this value is not provided, the SDK will
     * just not send any events.
     */
    public var dsn: String? = null

    /**
     * When enabled, stack traces are automatically attached to all threads logged. Stack traces are
     * always attached to exceptions but when this is set stack traces are also sent with threads. If
     * no threads are logged, we log the current thread automatically.
     */
    public var attachStackTrace: Boolean = true

    /** When enabled, all the threads are automatically attached to all logged events. */
    public var attachThreads: Boolean = true

    /** Sets the release. SDK will try to automatically configure a release out of the box */
    public var release: String? = null

    /**
     * Turns debug mode on or off. If debug is enabled SDK will attempt to print out useful debugging
     * information if something goes wrong. Default is disabled.
     */
    public var debug: Boolean = false

    /**
     * Sets the environment. This string is freeform and not set by default. A release can be
     * associated with more than one environment to separate them in the UI Think staging vs prod or
     * similar.
     */
    public var environment: String? = null

    /** Sets the distribution. Think about it together with release and environment */
    public var dist: String? = null

    /** Whether to enable or disable automatic session tracking.  */
    public var enableAutoSessionTracking: Boolean = true

    /**
     * The session tracking interval in millis. This is the interval to end a session if the App goes
     * to the background.
     */
    public var sessionTrackingIntervalMillis: Long = DEFAULT_SESSION_INTERVAL_MILLIS

    /**
     * Enables/Disables capturing screenshots before an error.
     * Available on iOS and Android.
     */
    public var attachScreenshot: Boolean = false

    /** Hook that is triggered before a breadcrumb is sent to Sentry */
    public var beforeBreadcrumb: ((Breadcrumb) -> Breadcrumb?)? = null

    /** Hook that is triggered before an event is sent to Sentry */
    public var beforeSend: ((SentryEvent) -> SentryEvent?)? = null

    /** Information about the Sentry SDK that generated this event. */
    public var sdk: SdkVersion? = null

    /** Sets the minimum log level. Default is [SentryLevel.DEBUG] */
    public var diagnosticLevel: SentryLevel = SentryLevel.DEBUG

    /** This variable controls the total amount of breadcrumbs that should be captured. Default is 100. */
    public var maxBreadcrumbs: Int = DEFAULT_MAX_BREADCRUMBS

    /** This variable controls the max attachment size in bytes */
    public var maxAttachmentSize: Long = DEFAULT_MAX_ATTACHMENT_SIZE

    /**
     * Enables or disables the attach view hierarchy feature when an error happened.
     * This is only available on iOS and Android.
     */
    public var attachViewHierarchy: Boolean = false

    /**
     * Enables or disables the feature to automatically capture HTTP client errors.
     * This is enabled by default.
     *
     * Available on Apple.
     */
    public var enableCaptureFailedRequests: Boolean = true

    /**
     * A list of HTTP status code ranges indicating which client errors should be captured as errors.
     *
     * By default, only HTTP client errors with a response code between 500 and 599 are captured as errors.
     *
     * Available on Apple.
     */
    public var failedRequestStatusCodes: List<HttpStatusCodeRange> = listOf(HttpStatusCodeRange())

    /**
     * A list of HTTP request targets indicating which client errors should be captured as errors with
     * either regex or a plain string.
     *
     * By default, HTTP client errors from every target (.* regular expression) are automatically captured.
     *
     * Available on Apple.
     */
    public var failedRequestTargets: List<String> = listOf(".*")

    /**
     * Configures the sample rate as a percentage of events to be sent in the range of 0.0 to 1.0. if
     * 1.0 is set it means that 100% of events are sent. If set to 0.1 only 10% of events will be
     * sent. Events are picked randomly. Default is null (disabled)
     */
    public var sampleRate: Double? = null

    /**
     * Configures the sample rate as a percentage of transactions to be sent in the range of 0.0 to
     * 1.0. if 1.0 is set it means that 100% of transactions are sent. If set to 0.1 only 10% of
     * transactions will be sent. Transactions are picked randomly. Default is null (disabled)
     */
    public var tracesSampleRate: Double? = null

    /**
     * Controls the tracking of App Hangs capturing moments when the application
     * becomes unresponsive for a set duration.
     *
     * **Default**: Enabled.
     *
     * **Platform Availability**: Cocoa.
     *
     * For more information, refer to the Cocoa documentation on App Hangs:
     * [Cocoa App Hangs](https://docs.sentry.io/platforms/apple/configuration/app-hangs/)
     */
    public var enableAppHangTracking: Boolean = true

    /**
     * Defines the timeout interval in milliseconds for detecting App Hangs.
     *
     * **Default**: 2000 milliseconds (2 seconds).
     *
     * **Platform Availability**: Cocoa.
     *
     * For configuration details and best practices, see:
     * [Cocoa App Hangs](https://docs.sentry.io/platforms/apple/configuration/app-hangs/)
     */
    public var appHangTimeoutIntervalMillis: Long = DEFAULT_APPHANG_TIMEOUT_INTERVAL_MILLIS

    /**
     * Enables or disables ANR (Application Not Responding) tracking.
     *
     * **Default**: Enabled.
     *
     * **Platform Availability**: Android.
     *
     * Detailed documentation on ANR tracking can be found here:
     * [Android ANR](https://docs.sentry.io/platforms/android/configuration/app-not-respond/)
     */
    public var isAnrEnabled: Boolean = true

    /**
     * Sets the timeout interval in milliseconds for considering an application as not responding (ANR).
     *
     * **Default**: 5000 milliseconds (5 seconds).
     *
     * **Platform Availability**: Android.
     *
     * For more information on configuring ANR detection, visit:
     * [Android ANR](https://docs.sentry.io/platforms/android/configuration/app-not-respond/)
     */
    public var anrTimeoutIntervalMillis: Long = DEFAULT_ANR_TIMEOUT_INTERVAL_MILLIS

    /**
     * Whether to enable Watchdog Termination tracking or not.
     *
     * **Default**: Enabled.
     *
     * **Platform Availability**: Cocoa.
     *
     */
    public var enableWatchdogTerminationTracking: Boolean = true

    /**
     * The options for session replay.
     * Currently available for **Android** and **iOS**.
     */
    public var sessionReplay: SentryReplayOptions = SentryReplayOptions()

    /**
     * If this flag is enabled, certain personally identifiable information (PII) is added by active integrations.
     * Among other things, enabling this will enable automatic IP address collection on events.
     *
     * If you enable this option, be sure to manually remove what you don't want to send using
     * our features for managing sensitive data.
     *
     * For further details, refer to the documentation in the respective native SDKs:
     * - [Cocoa](https://docs.sentry.io/platforms/apple/data-management/data-collected/)
     * - [Android](https://docs.sentry.io/platforms/android/data-management/data-collected/)
     */
    public var sendDefaultPii: Boolean = false

    /**
     * Experimental options for new features, these options are going to be promoted to SentryOptions
     * before GA.
     *
     * Beware that experimental options can change at any time.
     */
    public var experimental: ExperimentalOptions = ExperimentalOptions()
        private set

    /**
     * Experimental options for new features, these options are going to be promoted to SentryOptions
     * before GA.
     *
     * Beware that experimental options can change at any time.
     */
    public class ExperimentalOptions
}
