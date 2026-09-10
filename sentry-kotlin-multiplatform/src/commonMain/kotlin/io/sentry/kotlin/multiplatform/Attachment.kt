package io.sentry.kotlin.multiplatform

/** An attachment to be sent along with the event. */
public expect class Attachment {

    /** The bytes of the attachment. */
    public val bytes: ByteArray?

    /**
     * The content type of the attachment.
     * The server infers "application/octet-stream" if not set.
     */
    public val contentType: String?

    /** The pathname string of the attachment. */
    public val pathname: String?

    /** The name of the attachment to display in Sentry */
    public val filename: String

    public companion object {
        public fun fromScreenshot(screenshotBytes: ByteArray): Attachment
    }

    public constructor(bytes: ByteArray, filename: String)

    public constructor(bytes: ByteArray, filename: String, contentType: String?)

    public constructor(pathname: String)

    public constructor(pathname: String, filename: String)

    public constructor(pathname: String, filename: String, contentType: String?)
}
