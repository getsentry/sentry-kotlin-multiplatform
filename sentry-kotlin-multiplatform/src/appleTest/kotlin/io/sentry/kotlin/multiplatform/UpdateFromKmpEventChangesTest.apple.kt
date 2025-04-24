package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.updateFromKmpEventChanges
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.Message
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.User
import kotlinx.cinterop.convert
import kotlin.test.Test
import kotlin.test.assertEquals

actual class UpdateFromKmpEventChangesTest {
    @Test
    actual fun `native value is untouched when before and after values are the same`() {
        val cocoaEvent = CocoaSentryEvent().apply {
            releaseName = "1.0"
            dist = "1"
            environment = "production"
            serverName = "localhost"
            platform = "java"
            logger = "random logger"
            level = 3.convert()
            message = CocoaMessage().apply {
                message = "My Message"
            }
            fingerprint = mutableListOf("custom-fingerprints")
            user = CocoaUser().apply {
                userId = "123"
            }
            breadcrumbs = mutableListOf(
                CocoaBreadcrumb().apply {
                    message = "My Breadcrumb"
                }
            )
            eventId = CocoaSentryId("2bcc030ccc994de890e058cf5a0527d8")
        }
        val beforeKmpEvent = SentryEvent()
        val afterKmpEvent = SentryEvent()

        val updatedCocoaEvent = cocoaEvent.updateFromKmpEventChanges(beforeKmpEvent, afterKmpEvent)

        assertEquals("1.0", updatedCocoaEvent.releaseName)
        assertEquals("1", updatedCocoaEvent.dist)
        assertEquals("production", updatedCocoaEvent.environment)
        assertEquals("localhost", updatedCocoaEvent.serverName)
        assertEquals("java", updatedCocoaEvent.platform)
        assertEquals("random logger", updatedCocoaEvent.logger)
        assertEquals(3.convert(), updatedCocoaEvent.level)
        assertEquals("My Message", updatedCocoaEvent.message!!.message)
        assertEquals(1, updatedCocoaEvent.fingerprint!!.size)
        assertEquals("custom-fingerprints", updatedCocoaEvent.fingerprint!!.first())
        assertEquals("123", updatedCocoaEvent.user!!.userId)
        assertEquals(1, updatedCocoaEvent.breadcrumbs!!.size)
        assertEquals("My Breadcrumb", (updatedCocoaEvent.breadcrumbs!!.first() as CocoaBreadcrumb).message)
        assertEquals(CocoaSentryId("2bcc030ccc994de890e058cf5a0527d8"), updatedCocoaEvent.eventId)
    }

    @Test
    actual fun `native value is updated when before and after values are different`() {
        val cocoaEvent = CocoaSentryEvent().apply {
            releaseName = "1.0"
            dist = "1"
            environment = "production"
            serverName = "localhost"
            platform = "java"
            logger = "random logger"
            level = 1.convert()
            message = CocoaMessage().apply {
                message = "My Message"
            }
            fingerprint = mutableListOf("custom-fingerprints")
            user = CocoaUser().apply {
                userId = "123"
            }
            breadcrumbs = mutableListOf(
                CocoaBreadcrumb().apply {
                    message = "My Breadcrumb"
                }
            )
            eventId = CocoaSentryId("2bcc030ccc994de890e058cf5a0527d8")
        }
        val beforeKmpEvent = SentryEvent(cocoaEvent)
        val afterKmpEvent = SentryEvent(cocoaEvent).apply {
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
                id = "1234"
            }
            breadcrumbs = mutableListOf(
                Breadcrumb().apply {
                    message = "My Breadcrumb"
                }
            )
            eventId = SentryId("8bcc030ccc994de890e058cf5a0527d9")
        }

        val updatedCocoaEvent = cocoaEvent.updateFromKmpEventChanges(beforeKmpEvent, afterKmpEvent)

        assertEquals("2.0", updatedCocoaEvent.releaseName)
        assertEquals("staging", updatedCocoaEvent.environment)
        assertEquals("www", updatedCocoaEvent.serverName)
        assertEquals("kotlin", updatedCocoaEvent.platform)
        assertEquals("kmp logger", updatedCocoaEvent.logger)
        assertEquals(0.convert(), updatedCocoaEvent.level)
        assertEquals("Another Message", updatedCocoaEvent.message?.message)
        assertEquals(1, updatedCocoaEvent.fingerprint!!.size)
        assertEquals("another-fingerprints", updatedCocoaEvent.fingerprint!!.first())
        assertEquals("1234", updatedCocoaEvent.user?.userId)
        assertEquals(1, updatedCocoaEvent.breadcrumbs?.size)
        assertEquals("My Breadcrumb", (updatedCocoaEvent.breadcrumbs?.first() as CocoaBreadcrumb).message)
        assertEquals(SentryId("8bcc030ccc994de890e058cf5a0527d9").toString(), updatedCocoaEvent.eventId.toString())
    }
}
