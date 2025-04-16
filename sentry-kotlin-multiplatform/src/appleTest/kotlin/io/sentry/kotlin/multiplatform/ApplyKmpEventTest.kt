package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.applyKmpEvent
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplyKmpEventTest {
    @Test
    fun `null kmp event values should not override the cocoa event values`() {
        val initialUser = CocoaUser().apply { userId = "testUser" }
        val initialMessage = CocoaMessage().apply { message = "testMessage" }
        val cocoaEvent = CocoaSentryEvent().apply {
            level = 1.toUInt()
            platform = "cocoa"
            releaseName = "1.0.0"
            message = initialMessage
            logger = "testLogger"
            environment = "production"
            user = initialUser
            serverName = "testServer"
            dist = "dist1"
        }
        val kmpEvent = SentryEvent() // All properties are null by default

        val result = cocoaEvent.applyKmpEvent(kmpEvent)

        assertEquals(1.toUInt(), result.level)
        assertEquals("cocoa", result.platform)
        assertEquals("1.0.0", result.releaseName)
        assertEquals(initialMessage.message, result.message?.message)
        assertEquals("testLogger", result.logger)
        assertEquals("production", result.environment)
        assertEquals(initialUser.userId, result.user?.userId)
        assertEquals("testServer", result.serverName)
        assertEquals("dist1", result.dist)
    }
}