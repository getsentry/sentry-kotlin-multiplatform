package io.sentry.kotlin.multiplatform

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class AttachmentTest {

    @Test
    fun `adding pathname to attachment returns correct values`() {
        val pathname = "test"
        val attachment = Attachment(pathname)

        assertEquals(pathname, attachment.pathname)
        assertEquals(pathname, attachment.filename)
        assertEquals(null, attachment.bytes)
        assertEquals(null, attachment.contentType)
    }

    @Test
    fun `adding pathname and filename to attachment returns correct values`() {
        val pathname = "test"
        val filename = "test.log"
        val attachment = Attachment(pathname, filename)

        assertEquals(pathname, attachment.pathname)
        assertEquals(filename, attachment.filename)
        assertEquals(null, attachment.bytes)
        assertEquals(null, attachment.contentType)
    }

    @Test
    fun `adding pathname and filename and contentType returns correct values`() {
        val pathname = "test"
        val filename = "test.log"
        val contentType = "application/json"
        val attachment = Attachment(pathname, filename, contentType)

        assertEquals(pathname, attachment.pathname)
        assertEquals(filename, attachment.filename)
        assertEquals(contentType, attachment.contentType)
        assertEquals(null, attachment.bytes)
    }

    @Test
    fun `adding bytes and filename returns correct values`() {
        val bytes = "test".encodeToByteArray()
        val filename = "test.log"
        val attachment = Attachment(bytes, filename)

        assertContentEquals(bytes, attachment.bytes)
        assertEquals(filename, attachment.filename)
        assertEquals(null, attachment.contentType)
        assertEquals(null, attachment.pathname)
    }

    @Test
    fun `adding bytes and filename and contentType returns correct values`() {
        val bytes = "image bytes".encodeToByteArray()
        val filename = "image.png"
        val contentType = "image/png"
        val attachment = Attachment(bytes, filename, contentType)

        assertContentEquals(bytes, attachment.bytes)
        assertEquals(filename, attachment.filename)
        assertEquals(contentType, attachment.contentType)
        assertEquals(null, attachment.pathname)
    }
}
