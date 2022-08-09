package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toByteArray
import io.sentry.kotlin.multiplatform.extensions.toNSData

actual class Attachment : IAttachment {

    private var attachment: CocoaAttachment

    actual override val filename: String
        get() = attachment.filename

    actual override val pathname: String?
        get() = attachment.path

    actual override val bytes: ByteArray?
        get() = attachment.data?.toByteArray()

    actual override val contentType: String?
        get() = attachment.contentType

    actual constructor(pathname: String) {
        attachment = CocoaAttachment(pathname)
    }

    actual constructor(pathname: String, filename: String) {
        attachment = CocoaAttachment(pathname, filename)
    }

    actual constructor(pathname: String, filename: String, contentType: String?) {
        contentType?.let { attachment = CocoaAttachment(pathname, filename, it) }
        attachment = CocoaAttachment(pathname, filename)
    }

    actual constructor(bytes: ByteArray, filename: String) {
        attachment = CocoaAttachment(bytes.toNSData(), filename)
    }

    actual constructor(bytes: ByteArray, filename: String, contentType: String?) {
        contentType?.let { attachment = CocoaAttachment(bytes.toNSData(), filename, it) }
        attachment = CocoaAttachment(bytes.toNSData(), filename)
    }
}
