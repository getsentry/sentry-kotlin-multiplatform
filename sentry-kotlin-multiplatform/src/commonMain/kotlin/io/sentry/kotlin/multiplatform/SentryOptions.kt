package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.SdkVersion

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
    public var sessionTrackingIntervalMillis: Long = 30000

    /**
     * Enables/Disables capturing screenshots before an error.
     * Available on iOS and Android.
     */
    public var attachScreenshot: Boolean = false

    /** Hook that is triggered before a breadcrumb is sent to Sentry */
    public var beforeBreadcrumb: ((Breadcrumb) -> Breadcrumb?)? = null

    /** Information about the Sentry SDK that generated this event. */
    public var sdk: SdkVersion? = null

    /** This variable controls the total amount of breadcrumbs that should be captured. Default is 100. */
    public var maxBreadcrumbs: Int = 100

    /** This variable controls the max attachment size in bytes */
    public var maxAttachmentSize: Long = 20 * 1024 * 1024
}
