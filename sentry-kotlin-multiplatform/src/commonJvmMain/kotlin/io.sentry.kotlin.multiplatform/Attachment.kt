package io.sentry.kotlin.multiplatform

actual class Attachment : IAttachment {

    var jvmAttachment: JvmAttachment

    actual override val filename: String
        get() = jvmAttachment.filename

    actual override val pathname: String?
        get() = jvmAttachment.pathname

    actual override val bytes: ByteArray?
        get() = jvmAttachment.bytes

    actual override val contentType: String?
        get() = jvmAttachment.contentType

    actual companion object {
        actual fun fromScreenshot(screenshotBytes: ByteArray): Attachment {
            return Attachment(screenshotBytes, "screenshot.png", "image/png")
        }
    }

    actual constructor(pathname: String) {
        jvmAttachment = JvmAttachment(pathname)
    }

    actual constructor(pathname: String, filename: String) {
        jvmAttachment = JvmAttachment(pathname, filename)
    }

    actual constructor(pathname: String, filename: String, contentType: String?) {
        jvmAttachment = JvmAttachment(pathname, filename, contentType)
    }

    actual constructor(bytes: ByteArray, filename: String) {
        jvmAttachment = JvmAttachment(bytes, filename)
    }

    actual constructor(bytes: ByteArray, filename: String, contentType: String?) {
        jvmAttachment = JvmAttachment(bytes, filename, contentType)
    }
}
