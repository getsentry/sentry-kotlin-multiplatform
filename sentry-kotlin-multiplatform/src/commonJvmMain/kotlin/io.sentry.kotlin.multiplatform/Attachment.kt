package io.sentry.kotlin.multiplatform

actual class Attachment : IAttachment {

    private var attachment: JvmAttachment

    actual override val filename: String
        get() = attachment.filename

    actual override val pathname: String?
        get() = attachment.pathname

    actual override val bytes: ByteArray?
        get() = attachment.bytes

    actual override val contentType: String?
        get() = attachment.contentType

    actual constructor(pathname: String) {
        attachment = JvmAttachment(pathname)
    }

    actual constructor(pathname: String, filename: String) {
        attachment = JvmAttachment(pathname, filename)
    }

    actual constructor(pathname: String, filename: String, contentType: String?) {
        attachment = JvmAttachment(pathname, filename, contentType)
    }

    actual constructor(bytes: ByteArray, filename: String) {
        attachment = JvmAttachment(bytes, filename)
    }

    actual constructor(bytes: ByteArray, filename: String, contentType: String?) {
        attachment = JvmAttachment(bytes, filename, contentType)
    }
}
