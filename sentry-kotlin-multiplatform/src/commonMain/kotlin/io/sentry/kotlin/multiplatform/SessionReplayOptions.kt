package io.sentry.kotlin.multiplatform

/**
 * Configuration options for session replay.
 */
public data class SentryReplayOptions(
    /**
     * Indicates the percentage in which the replay for the session will be created.
     * Specifying 0 means never, 1.0 means always. The value needs to be >= 0.0 and <= 1.0.
     *
     * The default is null (disabled).
     */
    public var sessionSampleRate: Double? = null,

    /**
     * Indicates the percentage in which a 30 seconds replay will be sent with error events.
     * Specifying 0 means never, 1.0 means always. The value needs to be >= 0.0 and <= 1.0.
     *
     * The default is null (disabled).
     */
    public var onErrorSampleRate: Double? = null,

    /**
     * Redact all text content. Draws a rectangle of text bounds with text color on top.
     *
     * The default is true.
     */
    public var redactAllText: Boolean = true,

    /**
     * Redact all image content. Draws a rectangle of image bounds with image's dominant color on top.
     *
     * The default is true.
     */
    public var redactAllImages: Boolean = true,
)

