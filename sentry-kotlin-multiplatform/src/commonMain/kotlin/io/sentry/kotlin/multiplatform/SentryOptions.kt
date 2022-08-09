package io.sentry.kotlin.multiplatform

open class SentryOptions {

    /**
     * The DSN tells the SDK where to send the events to. If this value is not provided, the SDK will
     * just not send any events.
     */
    var dsn: String? = null

    /**
     * When enabled, stack traces are automatically attached to all threads logged. Stack traces are
     * always attached to exceptions but when this is set stack traces are also sent with threads. If
     * no threads are logged, we log the current thread automatically.
     */
    var attachStackTrace = true

    /** When enabled, all the threads are automatically attached to all logged events. */
    var attachThreads = true

    /** Sets the release. SDK will try to automatically configure a release out of the box */
    var release: String? = null

    /**
     * The session tracking interval in millis. This is the interval to end a session if the App goes
     * to the background.
     */
    var sessionTrackingIntervalMillis: Long = 30000

    /** Whether to enable or disable automatic session tracking. */
    var enableAutoSessionTracking = true

    /**
     * Turns debug mode on or off. If debug is enabled SDK will attempt to print out useful debugging
     * information if something goes wrong. Default is disabled.
     */
    var debug: Boolean = false

    /**
     * Sets the environment. This string is freeform and not set by default. A release can be
     * associated with more than one environment to separate them in the UI Think staging vs prod or
     * similar.
     */
    var environment: String? = null

    /** Sets the distribution. Think about it together with release and environment */
    var dist: String? = null

    /**
     * Enable or disable ANR (Application Not Responding) Default is enabled Used by AnrIntegration
     */
    var anrEnabled = true

    /** ANR Timeout interval in Millis Default is 5000 = 5s Used by AnrIntegration */
    var anrTimeoutIntervalMillis: Long = 5000

    /** Enables or disables the attach screenshot feature when an error happened.  */
    var attachScreenshot = false
}
