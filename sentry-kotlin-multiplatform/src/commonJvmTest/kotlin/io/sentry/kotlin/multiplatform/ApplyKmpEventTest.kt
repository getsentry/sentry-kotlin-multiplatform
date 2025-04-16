package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.applyKmpEvent
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplyKmpEventTest {
    @Test
    fun `null kmp event values should not override the Jvm event values`() {
        val initialUser = JvmUser().apply { id = "testUser" }
        val initialMessage = JvmMessage().apply { message = "testMessage" }
        val jvmEvent = JvmSentryEvent().apply {
            level = JvmSentryLevel.FATAL
            platform = "Jvm"
            release = "1.0.0"
            message = initialMessage
            logger = "testLogger"
            environment = "production"
            user = initialUser
            serverName = "testServer"
            dist = "dist1"
        }
        val kmpEvent = SentryEvent() // All properties are null by default

        val result = jvmEvent.applyKmpEvent(kmpEvent)

        assertEquals(JvmSentryLevel.FATAL, result.level)
        assertEquals("Jvm", result.platform)
        assertEquals("1.0.0", result.release)
        assertEquals(initialMessage.message, result.message?.message)
        assertEquals("testLogger", result.logger)
        assertEquals("production", result.environment)
        assertEquals(initialUser.id, result.user?.id)
        assertEquals("testServer", result.serverName)
        assertEquals("dist1", result.dist)
    }
}