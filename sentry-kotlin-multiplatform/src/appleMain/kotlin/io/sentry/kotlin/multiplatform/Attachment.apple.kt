package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toByteArray
import io.sentry.kotlin.multiplatform.extensions.toNSData

public actual class Attachment {

    internal lateinit var cocoaAttachment: CocoaAttachment

    public actual val filename: String
        get() = cocoaAttachment.filename

    public actual val pathname: String?
        get() = cocoaAttachment.path

    public actual val bytes: ByteArray?
        get() = cocoaAttachment.data?.toByteArray()

    public actual val contentType: String?
        get() = cocoaAttachment.contentType

    public actual companion object {
        public actual fun fromScreenshot(screenshotBytes: ByteArray): Attachment {
            return Attachment(screenshotBytes, "screenshot.png", "image/png")
        }
    }

    public actual constructor(pathname: String) {
        cocoaAttachment = CocoaAttachment(pathname)
    }

    public actual constructor(pathname: String, filename: String) {
        cocoaAttachment = CocoaAttachment(pathname, filename)
    }

    public actual constructor(pathname: String, filename: String, contentType: String?) {
        contentType?.let { cocoaAttachment = CocoaAttachment(pathname, filename, it) } ?: run {
            cocoaAttachment = CocoaAttachment(pathname, filename)
        }
    }

    public actual constructor(bytes: ByteArray, filename: String) {
        cocoaAttachment = CocoaAttachment(bytes.toNSData(), filename)
    }

    public actual constructor(bytes: ByteArray, filename: String, contentType: String?) {
        contentType?.let { cocoaAttachment = CocoaAttachment(bytes.toNSData(), filename, it) } ?: run {
            cocoaAttachment = CocoaAttachment(bytes.toNSData(), filename)
        }
    }
}
