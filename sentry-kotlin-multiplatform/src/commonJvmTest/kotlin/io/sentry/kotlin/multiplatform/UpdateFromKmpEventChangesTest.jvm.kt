package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.updateFromKmpEventChanges
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.Message
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.User
import kotlin.test.Test
import kotlin.test.assertEquals

actual class UpdateFromKmpEventChangesTest {
    @Test
    actual fun `native value is untouched when before and after values are the same`() {
        val jvmEvent = JvmSentryEvent().apply {
            release = "1.0"
            dist = "1"
            environment = "production"
            serverName = "localhost"
            platform = "java"
            logger = "random logger"
            level = JvmSentryLevel.FATAL
            message = JvmMessage().apply {
                message = "My Message"
            }
            fingerprints = mutableListOf("custom-fingerprints")
            user = io.sentry.protocol.User().apply {
                id = "123"
            }
            breadcrumbs = mutableListOf(
                JvmBreadcrumb().apply {
                    message = "My Breadcrumb"
                }
            )
            eventId = JvmSentryId("2bcc030ccc994de890e058cf5a0527d8")
        }
        val beforeKmpEvent = SentryEvent()
        val afterKmpEvent = SentryEvent()

        val updatedJvmEvent = jvmEvent.updateFromKmpEventChanges(beforeKmpEvent, afterKmpEvent)

        assertEquals("1.0", updatedJvmEvent.release)
        assertEquals("1", updatedJvmEvent.dist)
        assertEquals("production", updatedJvmEvent.environment)
        assertEquals("localhost", updatedJvmEvent.serverName)
        assertEquals("java", updatedJvmEvent.platform)
        assertEquals("random logger", updatedJvmEvent.logger)
        assertEquals(JvmSentryLevel.FATAL, updatedJvmEvent.level)
        assertEquals("My Message", updatedJvmEvent.message!!.message)
        assertEquals(1, updatedJvmEvent.fingerprints!!.size)
        assertEquals("custom-fingerprints", updatedJvmEvent.fingerprints!!.first())
        assertEquals("123", updatedJvmEvent.user!!.id)
        assertEquals(1, updatedJvmEvent.breadcrumbs!!.size)
        assertEquals("My Breadcrumb", updatedJvmEvent.breadcrumbs!!.first().message)
        assertEquals(JvmSentryId("2bcc030ccc994de890e058cf5a0527d8"), updatedJvmEvent.eventId)
    }

    @Test
    actual fun `native value is updated when before and after values are different`() {
        val jvmEvent = JvmSentryEvent().apply {
            release = "1.0"
            dist = "1"
            environment = "production"
            serverName = "localhost"
            platform = "java"
            logger = "random logger"
            level = JvmSentryLevel.FATAL
            message = JvmMessage().apply {
                message = "My Message"
            }
            fingerprints = mutableListOf("custom-fingerprints")
            user = io.sentry.protocol.User().apply {
                id = "123"
            }
            breadcrumbs = mutableListOf(
                JvmBreadcrumb().apply {
                    message = "My Breadcrumb"
                }
            )
            eventId = JvmSentryId("2bcc030ccc994de890e058cf5a0527d8")
        }
        val beforeKmpEvent = SentryEvent(jvmEvent)
        val afterKmpEvent = SentryEvent(jvmEvent).apply {
            release = "2.0"
            dist = "123"
            environment = "staging"
            serverName = "www"
            platform = "kotlin"
            logger = "kmp logger"
            level = SentryLevel.DEBUG
            message = Message().apply {
                message = "Another Message"
            }
            fingerprint = mutableListOf("another-fingerprints")
            user = User().apply {
                id = "123"
            }
            breadcrumbs = mutableListOf(
                Breadcrumb().apply {
                    message = "My Breadcrumb"
                }
            )
            eventId = SentryId("8bcc030ccc994de890e058cf5a0527d9")
        }

        val updatedJvmEvent = jvmEvent.updateFromKmpEventChanges(beforeKmpEvent, afterKmpEvent)

        assertEquals("2.0", updatedJvmEvent.release)
        assertEquals("staging", updatedJvmEvent.environment)
        assertEquals("www", updatedJvmEvent.serverName)
        assertEquals("kotlin", updatedJvmEvent.platform)
        assertEquals("kmp logger", updatedJvmEvent.logger)
        assertEquals(JvmSentryLevel.DEBUG, updatedJvmEvent.level)
        assertEquals("Another Message", updatedJvmEvent.message?.message)
        assertEquals(1, updatedJvmEvent.fingerprints!!.size)
        assertEquals("another-fingerprints", updatedJvmEvent.fingerprints!!.first())
        assertEquals("123", updatedJvmEvent.user?.id)
        assertEquals(1, updatedJvmEvent.breadcrumbs?.size)
        assertEquals("My Breadcrumb", updatedJvmEvent.breadcrumbs?.first()?.message)
        assertEquals(SentryId("8bcc030ccc994de890e058cf5a0527d9").toString(), updatedJvmEvent.eventId.toString())
    }
}
