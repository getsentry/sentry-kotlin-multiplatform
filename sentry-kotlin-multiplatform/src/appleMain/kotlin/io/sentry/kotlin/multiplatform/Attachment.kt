package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toByteArray
import io.sentry.kotlin.multiplatform.extensions.toNSData

actual class Attachment : IAttachment {

    lateinit var cocoaAttachment: CocoaAttachment

    actual override val filename: String
        get() = cocoaAttachment.filename

    actual override val pathname: String?
        get() = cocoaAttachment.path

    actual override val bytes: ByteArray?
        get() = cocoaAttachment.data?.toByteArray()

    actual override val contentType: String?
        get() = cocoaAttachment.contentType

    actual companion object {
        actual fun fromScreenshot(screenshotBytes: ByteArray): Attachment { val data = screenshotBytes.toNSData()
            return Attachment(screenshotBytes, "screenshot.png", "image/png") }
    }

    actual constructor(pathname: String) {
        cocoaAttachment = CocoaAttachment(pathname)
    }

    actual constructor(pathname: String, filename: String) {
        cocoaAttachment = CocoaAttachment(pathname, filename)
    }

    actual constructor(pathname: String, filename: String, contentType: String?) {
        contentType?.let { cocoaAttachment = CocoaAttachment(pathname, filename, it) } ?: run {
            cocoaAttachment = CocoaAttachment(pathname, filename)
        }
    }

    actual constructor(bytes: ByteArray, filename: String) {
        cocoaAttachment = CocoaAttachment(bytes.toNSData(), filename)
    }

    actual constructor(bytes: ByteArray, filename: String, contentType: String?) {
        contentType?.let { cocoaAttachment = CocoaAttachment(bytes.toNSData(), filename, it) } ?: run {
            cocoaAttachment = CocoaAttachment(bytes.toNSData(), filename)
        }
    }
}
