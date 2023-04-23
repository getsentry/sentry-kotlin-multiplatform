package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.User
import io.sentry.kotlin.multiplatform.utils.dsn
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SentryIntegrationTest {
    @Test
    fun `captureMessage sends the correct message`() {
        val expected = "test"
        var actual = ""
        Sentry.init {
            it.dsn = dsn
            it.beforeSend = { event ->
                actual = event.message?.formatted ?: ""
                null
            }
        }

        Sentry.captureMessage(expected)
        assertEquals(expected, actual)
        assertNotEquals("test2", actual)
    }

    @Test
    fun `captureException sends the correct exceptions`() {
        val capturedEvents = mutableListOf<SentryEvent>()
        Sentry.init {
            it.dsn = dsn
            it.beforeSend = { event ->
                capturedEvents.add(event)
                null
            }
        }

        Sentry.captureException(RuntimeException("test"))
        Sentry.captureException(RuntimeException("test2"))

        // contains the events from the previous two calls
        assertEquals(2, capturedEvents.size)

        // the first event
        val event = capturedEvents[0]
        event.exceptions.first().type?.let { assertTrue(it.contains("RuntimeException")) }
        assertEquals("test", event.exceptions.first().value)
        assertNotEquals("test2", event.exceptions.first().value)

        // the second event
        val event2 = capturedEvents[1]
        event2.exceptions.first().type?.let { assertTrue(it.contains("RuntimeException")) }
        assertEquals("test2", event2.exceptions.first().value)
        assertNotEquals("test", event2.exceptions.first().value)
    }

    @Test
    fun `capturing no events is correct`() {
        val capturedEvents = mutableListOf<SentryEvent>()
        Sentry.init {
            it.dsn = dsn
            it.beforeSend = { event ->
                capturedEvents.add(event)
                null
            }
        }

        assertEquals(0, capturedEvents.size)
        assertNotEquals(1, capturedEvents.size)
    }

    @Test
    fun `beforeBreadcrumb receives breadcrumbs correctly`() {
        val breadcrumb = Breadcrumb(message = "test")
        val breadcrumbs = mutableListOf<Breadcrumb>()
        Sentry.init {
            it.dsn = dsn
            it.beforeBreadcrumb = { breadcrumb ->
                breadcrumbs.add(breadcrumb)
                null
            }
        }

        Sentry.addBreadcrumb(breadcrumb)
        Sentry.captureException(RuntimeException("test"))

        assertTrue { breadcrumbs.any { it.message == "test" } }
        assertFalse { breadcrumbs.any { it.message == "test2" } }
    }

    @Test
    fun `scope is correct when modified with capturing an event`() {
        val expectedKey = "testABC"
        val expectedValue = "valueABC"
        var actualKey = ""
        var actualValue = ""
        Sentry.init {
            it.dsn = dsn
            it.beforeSend = { event ->
                actualKey = event.tags.keys.first()
                actualValue = event.tags.values.first()
                null
            }
        }

        Sentry.captureException(RuntimeException("test")) {
            it.setTag(expectedKey, expectedValue)
        }

        assertEquals(expectedKey, actualKey)
        assertEquals(expectedValue, actualValue)
        assertNotEquals("testABC2", actualKey)
        assertNotEquals("valueABC2", actualValue)
    }

    @Test
    fun `local scope overrides global scope when capturing an event and then capturing another event`() {
        val expectedKey = "testABC"
        val expectedValue = "valueABC"
        var actualKey = ""
        var actualValue = ""
        Sentry.init {
            it.dsn = dsn
            it.beforeSend = { event ->
                actualKey = event.tags.keys.first()
                actualValue = event.tags.values.first()
                null
            }
        }

        Sentry.captureException(RuntimeException("test")) {
            it.setTag("testABC", "valueABC")
        }

        Sentry.captureException(RuntimeException("test2")) {
            it.setTag("testABC", "valueABC")
        }

        assertEquals(expectedKey, actualKey)
        assertEquals(expectedValue, actualValue)
        assertNotEquals("testABC2", actualKey)
        assertNotEquals("valueABC2", actualValue)
    }

    @Test
    fun `global scope sets tag correctly`() {
        val expectedKey = "testABC"
        val expectedValue = "valueABC"
        var actualKey = ""
        var actualValue = ""
        Sentry.init {
            it.dsn = dsn
            it.beforeSend = { event ->
                actualKey = event.tags.keys.first()
                actualValue = event.tags.values.first()
                null
            }
        }

        Sentry.configureScope {
            it.setTag("testABC", "valueABC")
        }

        Sentry.captureException(RuntimeException("test"))

        assertEquals(expectedKey, actualKey)
        assertEquals(expectedValue, actualValue)
        assertNotEquals("testABC2", actualKey)
        assertNotEquals("valueABC2", actualValue)
    }

    @Test
    fun `global scope sets level correctly`() {
        val expectedLevel = SentryLevel.DEBUG
        var actualLevel: SentryLevel? = null
        Sentry.init {
            it.dsn = dsn
            it.beforeSend = { event ->
                actualLevel = event.level
                null
            }
        }

        Sentry.configureScope {
            it.level = expectedLevel
        }

        Sentry.captureException(RuntimeException("test"))

        assertNotNull(actualLevel)
        assertEquals(expectedLevel, actualLevel)
        assertNotEquals(SentryLevel.INFO, actualLevel)
    }

    @Test
    fun `global scope sets user correctly`() {
        val expectedEmail = "test@example.com"
        val expectedId = "123"
        val expectedIpAddress = "127.0.0.1"
        val expectedUsername = "testuser"
        var actualEmail = ""
        var actualId = ""
        var actualIpAddress = ""
        var actualUsername = ""

        Sentry.init {
            it.dsn = dsn
            it.beforeSend = { event ->
                val user = event.user
                actualEmail = user?.email ?: ""
                actualId = user?.id ?: ""
                actualIpAddress = user?.ipAddress ?: ""
                actualUsername = user?.username ?: ""
                null
            }
        }

        Sentry.configureScope {
            it.user = User().apply {
                this.email = expectedEmail
                this.id = expectedId
                this.ipAddress = expectedIpAddress
                this.username = expectedUsername
            }
        }

        Sentry.captureException(RuntimeException("test"))

        assertEquals(expectedEmail, actualEmail)
        assertEquals(expectedId, actualId)
        assertEquals(expectedIpAddress, actualIpAddress)
        assertEquals(expectedUsername, actualUsername)
    }

    @Test
    fun `global scope sets context correctly with different data types`() = runTest {
        val stringKey = "stringKey"
        val stringValue = "stringValue"
        val booleanKey = "booleanKey"
        val booleanValue = true
        val numberKey = "numberKey"
        val numberValue = 123
        val collectionKey = "collectionKey"
        val collectionValue = listOf("abc", 123, true)

        val deferred = CompletableDeferred<Unit>()
        Sentry.init {
            it.dsn = dsn
            it.beforeSend = { event ->
                val contexts = event.contexts
                try {
                    assertNotNull(contexts)
                    assertEquals(mapOf("value" to stringValue), contexts[stringKey])
                    assertEquals(mapOf("value" to booleanValue), contexts[booleanKey])
                    assertEquals(mapOf("value" to numberValue), contexts[numberKey])
                    assertEquals(mapOf("value" to collectionValue), contexts[collectionKey])
                    deferred.complete(Unit)
                } catch (e: Throwable) {
                    deferred.completeExceptionally(e)
                }
                null
            }
        }

        Sentry.configureScope {
            it.setContext(stringKey, stringValue)
            it.setContext(booleanKey, booleanValue)
            it.setContext(numberKey, numberValue)
            it.setContext(collectionKey, collectionValue)
        }

        Sentry.captureException(RuntimeException("test"))
        deferred.await()
    }
}