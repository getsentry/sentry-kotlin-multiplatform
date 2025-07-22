package io.sentry.kotlin.multiplatform

public actual class Attachment {

    public actual val bytes: ByteArray?
    public actual val contentType: String?
    public actual val pathname: String?
    public actual val filename: String

    public actual companion object {
        public actual fun fromScreenshot(screenshotBytes: ByteArray): Attachment =
            Attachment(screenshotBytes, "screenshot.png", "image/png")
    }

    public actual constructor(bytes: ByteArray, filename: String) {
        this.bytes = null
        this.pathname = null
        this.filename = ""
        this.contentType = null
    }

    public actual constructor(bytes: ByteArray, filename: String, contentType: String?) {
        this.bytes = null
        this.pathname = null
        this.filename = ""
        this.contentType = null
    }

    public actual constructor(pathname: String) {
        this.bytes = null
        this.pathname = null
        this.filename = ""
        this.contentType = null
    }

    public actual constructor(pathname: String, filename: String) {
        this.bytes = null
        this.pathname = null
        this.filename = ""
        this.contentType = null
    }

    public actual constructor(pathname: String, filename: String, contentType: String?) {
        this.bytes = null
        this.pathname = null
        this.filename = ""
        this.contentType = null
    }
}
