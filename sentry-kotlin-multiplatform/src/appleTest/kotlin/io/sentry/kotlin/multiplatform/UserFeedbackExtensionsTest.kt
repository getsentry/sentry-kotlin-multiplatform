package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.serialize
import io.sentry.kotlin.multiplatform.extensions.toCocoaUserFeedback
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.UserFeedback
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class UserFeedbackExtensionsTest {
    private val sentryIdString = "dcebada57d794590a6da3d1977eed58a"

    @Test
    fun `feedback maps message contact fields source and associated event`() {
        val feedback =
            UserFeedback(SentryId(sentryIdString))
                .apply {
                    comments = "I had an error"
                    name = "John Doe"
                    email = "john@doe.com"
                }.toCocoaUserFeedback()

        assertEquals(
            mapOf<Any?, Any?>(
                "message" to "I had an error",
                "name" to "John Doe",
                "contact_email" to "john@doe.com",
                "source" to "custom",
                "associated_event_id" to sentryIdString,
            ),
            feedback.serialize(),
        )
        assertNotEquals(sentryIdString, feedback.eventId().sentryIdString())
    }

    @Test
    fun `null comments become an empty message and absent contact fields stay absent`() {
        val feedback = UserFeedback(SentryId(sentryIdString)).toCocoaUserFeedback().serialize()

        assertEquals("", feedback["message"])
        assertEquals("custom", feedback["source"])
        assertEquals(sentryIdString, feedback["associated_event_id"])
        assertFalse(feedback.containsKey("name"))
        assertFalse(feedback.containsKey("contact_email"))
    }

    @Test
    fun `explicitly empty comments and contact fields are preserved`() {
        val feedback =
            UserFeedback(SentryId(sentryIdString))
                .apply {
                    comments = ""
                    name = ""
                    email = ""
                }.toCocoaUserFeedback()
                .serialize()

        assertEquals("", feedback["message"])
        assertEquals("", feedback["name"])
        assertEquals("", feedback["contact_email"])
    }
}
