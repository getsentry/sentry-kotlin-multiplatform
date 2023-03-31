package io.sentry.kotlin.multiplatform

public actual class Attachment {

    internal var jvmAttachment: JvmAttachment

    public actual val filename: String
        get() = jvmAttachment.filename

    public actual val pathname: String?
        get() = jvmAttachment.pathname

    public actual val bytes: ByteArray?
        get() = jvmAttachment.bytes

    public actual val contentType: String?
        get() = jvmAttachment.contentType

    public actual companion object {
        public actual fun fromScreenshot(screenshotBytes: ByteArray): Attachment {
            return Attachment(screenshotBytes, "screenshot.png", "image/png")
        }
    }

    public actual constructor(pathname: String) {
        jvmAttachment = JvmAttachment(pathname)
    }

    public actual constructor(pathname: String, filename: String) {
        jvmAttachment = JvmAttachment(pathname, filename)
    }

    public actual constructor(pathname: String, filename: String, contentType: String?) {
        jvmAttachment = JvmAttachment(pathname, filename, contentType)
    }

    public actual constructor(bytes: ByteArray, filename: String) {
        jvmAttachment = JvmAttachment(bytes, filename)
    }

    public actual constructor(bytes: ByteArray, filename: String, contentType: String?) {
        jvmAttachment = JvmAttachment(bytes, filename, contentType)
    }
}
