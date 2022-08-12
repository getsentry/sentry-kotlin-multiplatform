package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.UserFeedback
import kotlin.test.Test
import kotlin.test.assertEquals

class UserFeedbackTest {

    private val sentryIdString = "dcebada57d794590a6da3d1977eed58a"

    @Test
    fun `UserFeedback with email has proper values`() {
        val userFeedback = UserFeedback(SentryId(sentryIdString))
        userFeedback.email = "test@email.com"

        assertEquals("test@email.com", userFeedback.email)
        assertEquals(null, userFeedback.name)
        assertEquals(null, userFeedback.comments)
    }

    @Test
    fun `UserFeedback with name has proper values`() {
        val userFeedback = UserFeedback(SentryId(sentryIdString))
        userFeedback.name = "John Doe"

        assertEquals("John Doe", userFeedback.name)
        assertEquals(null, userFeedback.email)
        assertEquals(null, userFeedback.comments)
    }

    @Test
    fun `UserFeedback with comment has proper values`() {
        val userFeedback = UserFeedback(SentryId(sentryIdString))
        userFeedback.comments = "I had an error during login"

        assertEquals("I had an error during login", userFeedback.comments)
        assertEquals(null, userFeedback.name)
        assertEquals(null, userFeedback.email)
    }
}
