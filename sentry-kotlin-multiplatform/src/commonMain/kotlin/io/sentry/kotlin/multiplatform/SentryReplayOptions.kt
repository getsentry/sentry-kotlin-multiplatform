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

    /**
     * Defines the quality of the session replay. The higher the quality, the more accurate the replay
     * will be, but also more data to transfer and more CPU load, defaults to MEDIUM.
     */
    public var quality: Quality = Quality.MEDIUM
) {
    public enum class Quality(
        /** The scale related to the window size (in dp) at which the replay will be created.  */
        public val sizeScale: Float,
        /**
         * Defines the quality of the session replay. Higher bit rates have better replay quality, but
         * also affect the final payload size to transfer, defaults to 40kbps.
         */
        public val bitRate: Int
    ) {
        /** Video Scale: 80% Bit Rate: 50.000  */
        LOW(0.8f, 50000),

        /** Video Scale: 100% Bit Rate: 75.000  */
        MEDIUM(1.0f, 75000),

        /** Video Scale: 100% Bit Rate: 100.000  */
        HIGH(1.0f, 100000)
    }
}
