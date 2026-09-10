package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toCocoaUserFeedback
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.UserFeedback
import kotlin.test.Test
import kotlin.test.assertEquals

class UserFeedbackExtensionsTest {

    private val sentryIdString = "dcebada57d794590a6da3d1977eed58a"

    @Test
    fun `toCocoaUserFeedback correctly maps comments`() {
        val userFeedback = UserFeedback(SentryId(sentryIdString)).apply {
            comments = "Test comment"
        }

        val cocoaUserFeedback = userFeedback.toCocoaUserFeedback()

        assertEquals("Test comment", cocoaUserFeedback.comments())
    }

    @Test
    fun `toCocoaUserFeedback correctly maps email`() {
        val userFeedback = UserFeedback(SentryId(sentryIdString)).apply {
            email = "test@email.com"
        }

        val cocoaUserFeedback = userFeedback.toCocoaUserFeedback()

        assertEquals("test@email.com", cocoaUserFeedback.email())
    }

    @Test
    fun `toCocoaUserFeedback correctly maps name`() {
        val userFeedback = UserFeedback(SentryId(sentryIdString)).apply {
            name = "John Doe"
        }

        val cocoaUserFeedback = userFeedback.toCocoaUserFeedback()

        assertEquals("John Doe", cocoaUserFeedback.name())
    }

    @Test
    fun `toCocoaUserFeedback keeps default empty string when comments is null`() {
        val userFeedback = UserFeedback(SentryId(sentryIdString))
        // comments is null by default

        val cocoaUserFeedback = userFeedback.toCocoaUserFeedback()

        // Cocoa SDK uses non-nullable NSString, so default is empty string
        assertEquals("", cocoaUserFeedback.comments())
    }

    @Test
    fun `toCocoaUserFeedback keeps default empty string when email is null`() {
        val userFeedback = UserFeedback(SentryId(sentryIdString))
        // email is null by default

        val cocoaUserFeedback = userFeedback.toCocoaUserFeedback()

        // Cocoa SDK uses non-nullable NSString, so default is empty string
        assertEquals("", cocoaUserFeedback.email())
    }

    @Test
    fun `toCocoaUserFeedback keeps default empty string when name is null`() {
        val userFeedback = UserFeedback(SentryId(sentryIdString))
        // name is null by default

        val cocoaUserFeedback = userFeedback.toCocoaUserFeedback()

        // Cocoa SDK uses non-nullable NSString, so default is empty string
        assertEquals("", cocoaUserFeedback.name())
    }

    @Test
    fun `toCocoaUserFeedback maps all properties correctly`() {
        val userFeedback = UserFeedback(SentryId(sentryIdString)).apply {
            name = "John Doe"
            email = "john@doe.com"
            comments = "I had an error"
        }

        val cocoaUserFeedback = userFeedback.toCocoaUserFeedback()

        assertEquals("John Doe", cocoaUserFeedback.name())
        assertEquals("john@doe.com", cocoaUserFeedback.email())
        assertEquals("I had an error", cocoaUserFeedback.comments())
    }
}
