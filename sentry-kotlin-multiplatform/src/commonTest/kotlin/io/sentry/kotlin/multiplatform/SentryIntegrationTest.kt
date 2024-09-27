package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.User
import io.sentry.kotlin.multiplatform.utils.fakeDsn
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class SentryIntegrationTest : BaseSentryTest() {
    @AfterTest
    fun tearDown() {
        Sentry.close()
    }

    @Test
    fun `init with native options is successful`() {
        val sentryPlatformOptions = createSentryPlatformOptionsConfiguration()
        sentryInitWithPlatformOptions(sentryPlatformOptions)
    }

    @Test
    fun `captureMessage sends the correct message`() {
        val expected = "test"
        var actual = ""
        sentryInit {
            it.dsn = fakeDsn
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
        sentryInit {
            it.dsn = fakeDsn
            it.beforeSend = { event ->
                capturedEvents.add(event)
                null
            }
        }

        Sentry.captureException(RuntimeException("test"))
        Sentry.captureException(RuntimeException("test2"))

        assertEquals(2, capturedEvents.size)

        val event = capturedEvents[0]
        event.exceptions.first().type?.let { assertTrue(it.contains("RuntimeException")) }
        assertEquals("test", event.exceptions.first().value)

        val event2 = capturedEvents[1]
        event2.exceptions.first().type?.let { assertTrue(it.contains("RuntimeException")) }
        assertEquals("test2", event2.exceptions.first().value)
    }

    @Test
    fun `capturing no events is correct`() {
        val capturedEvents = mutableListOf<SentryEvent>()
        sentryInit {
            it.dsn = fakeDsn
            it.beforeSend = { event ->
                capturedEvents.add(event)
                null
            }
        }

        assertEquals(0, capturedEvents.size)
    }

    @Test
    fun `beforeBreadcrumb receives breadcrumbs correctly`() {
        val breadcrumb = Breadcrumb(message = "test")
        val breadcrumbs = mutableListOf<Breadcrumb>()
        sentryInit {
            it.dsn = fakeDsn
            it.beforeBreadcrumb = { breadcrumb ->
                breadcrumbs.add(breadcrumb)
                null
            }
        }

        Sentry.addBreadcrumb(breadcrumb)
        Sentry.captureException(RuntimeException("test"))

        assertTrue { breadcrumbs.any { it.message == "test" } }
    }

    @Test
    fun `scope is correct when modified with capturing an event`() {
        val expectedKey = "testABC"
        val expectedValue = "valueABC"
        var actualValue = ""
        sentryInit {
            it.dsn = fakeDsn
            it.beforeSend = { event ->
                actualValue = event.tags[expectedKey] ?: ""
                null
            }
        }

        Sentry.captureException(RuntimeException("test")) {
            it.setTag(expectedKey, expectedValue)
        }

        assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `local scope overrides global scope when capturing an event and then capturing another event`() {
        val expectedKey = "testABC"
        val expectedValue = "valueABC"
        var actualValue = ""
        sentryInit {
            it.dsn = fakeDsn
            it.beforeSend = { event ->
                actualValue = event.tags[expectedKey] ?: ""
                null
            }
        }

        Sentry.captureException(RuntimeException("test")) {
            it.setTag("testABC", "valueABC")
        }

        Sentry.captureException(RuntimeException("test2")) {
            it.setTag("testABC", "valueABC")
        }

        assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `global scope sets tag correctly`() {
        val expectedKey = "testABC"
        val expectedValue = "valueABC"
        var actualValue = ""
        sentryInit {
            it.dsn = fakeDsn
            it.beforeSend = { event ->
                actualValue = event.tags[expectedKey] ?: ""
                null
            }
        }

        Sentry.configureScope {
            it.setTag("testABC", "valueABC")
        }

        Sentry.captureException(RuntimeException("test"))

        assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `global scope sets level correctly`() {
        val expectedLevel = SentryLevel.DEBUG
        var actualLevel: SentryLevel? = null
        sentryInit {
            it.dsn = fakeDsn
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

        sentryInit {
            it.dsn = fakeDsn
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
    fun `isEnabled returns true when SDK is enabled`() {
        sentryInit {
            it.dsn = fakeDsn
        }

        assertTrue(Sentry.isEnabled())
    }

    @Test
    fun `isEnabled returns false when SDK is disabled`() {
        assertFalse(Sentry.isEnabled())
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

        val expectedStringValue = mapOf("value" to stringValue)
        val expectedBooleanValue = mapOf("value" to booleanValue)
        val expectedNumberValue = mapOf("value" to numberValue)
        val expectedCollectionValue = mapOf("value" to collectionValue)

        var actualStringValue: Map<String, Any>? = null
        var actualBooleanValue: Map<String, Any>? = null
        var actualNumberValue: Map<String, Any>? = null
        var actualCollectionValue: Map<String, Any>? = null

        sentryInit {
            it.dsn = fakeDsn
            it.beforeSend = { event ->
                val contexts = event.contexts
                assertNotNull(contexts)
                actualStringValue = contexts[stringKey] as Map<String, Any>?
                actualBooleanValue = contexts[booleanKey] as Map<String, Any>?
                actualNumberValue = contexts[numberKey] as Map<String, Any>?
                actualCollectionValue = contexts[collectionKey] as Map<String, Any>?
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

        assertEquals(expectedStringValue, actualStringValue)
        assertEquals(expectedBooleanValue, actualBooleanValue)
        assertEquals(expectedNumberValue, actualNumberValue)
        assertEquals(expectedCollectionValue, actualCollectionValue)
    }
}
