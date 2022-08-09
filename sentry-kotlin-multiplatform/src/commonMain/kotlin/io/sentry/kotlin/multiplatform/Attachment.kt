package io.sentry.kotlin.multiplatform

expect class Attachment : IAttachment {

    override val filename: String

    override val bytes: ByteArray?

    override val contentType: String?

    override val pathname: String?

    companion object {
        fun fromScreenshot(screenshotBytes: ByteArray): Attachment
    }

    constructor(bytes: ByteArray, filename: String)

    constructor(bytes: ByteArray, filename: String, contentType: String?)

    constructor(pathname: String)

    constructor(pathname: String, filename: String)

    constructor(pathname: String, filename: String, contentType: String?)
}

interface IAttachment {

    /** The bytes of the attachment. */
    val bytes: ByteArray?

    /**
     * The content type of the attachment.
     * The server infers "application/octet-stream" if not set.
     */
    val contentType: String?

    /** The pathname string of the attachment. */
    val pathname: String?

    /** The name of the attachment to display in Sentry */
    val filename: String
}
