package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.User
import kotlin.test.*

class SentryScopeTest : BaseSentryScopeTest() {

    private val testUsername = "MyUsername"
    private val testEmail = "Email"
    private val testId = "TestId"
    private val testIpAddress = "0.0.1.0"

    private var sentryScope: SentryScope? = null
    private var user: User? = null

    @BeforeTest
    fun setup() {
        sentryScope = initializeScope()
        user = createTestUser()
    }

    @AfterTest
    fun tearDown() {
        sentryScope = null
        user = null
    }

    private fun createTestUser(): User {
        return User().apply {
            username = testUsername
            email = testEmail
            id = testId
            ipAddress = testIpAddress
        }
    }

    @Test
    fun `adding user to scope should properly persist user in scope`() {
        sentryScope?.user = user

        assertEquals(user, sentryScope?.user)
    }

    @Test
    fun `modifying user in scope does not persist`() {
        sentryScope?.user = user
        sentryScope?.user?.username = "Test Username"

        assertEquals(testUsername, sentryScope?.user?.username)
    }

    @Test
    fun `adding tags in scope should be set correctly`() {
        sentryScope?.setTag("key", "value")
        sentryScope?.setTag("key2", "value2")

        sentryScope?.getTags()?.let {
            assertEquals(it["key"], "value")
            assertEquals(it["key2"], "value2")
        }
    }

    @Test
    fun `string value context should be set correctly`() {
        val stringContext = mapOf("value" to "Test")
        val expectedContext = mapOf("Context" to stringContext)
        sentryScope?.setContext("Context", "Test")

        assertEquals(expectedContext as Map<String, Any>?, sentryScope?.getContexts())
    }

    @Test
    fun `number value context should be set correctly`() {
        val numberContext = mapOf("value" to 12)
        val expectedContext = mapOf("Context" to numberContext)
        sentryScope?.setContext("Context", 12)

        assertEquals(expectedContext as Map<String, Any>?, sentryScope?.getContexts())
    }

    @Test
    fun `boolean value context should be set correctly`() {
        val booleanContext = mapOf("value" to false)
        val expectedContext = mapOf("Context" to booleanContext)
        sentryScope?.setContext("Context", false)

        assertEquals(expectedContext as Map<String, Any>?, sentryScope?.getContexts())
    }

    @Test
    fun `array value context should be set correctly`() {
        val array = arrayOf(1, 2, 3, "2")
        val arrayContext = mapOf("value" to array)
        val expectedContext = mapOf("Context" to arrayContext)
        sentryScope?.setContext("Context", array)

        assertEquals(expectedContext as Map<String, Any>?, sentryScope?.getContexts())
    }

    @Test
    fun `collection set value context should be set correctly`() {
        val set = setOf(1, 2, "2", 4)
        val setContext = mapOf("value" to set)
        val expectedContext = mapOf("Context" to setContext)
        sentryScope?.setContext("Context", set)

        assertEquals(expectedContext as Map<String, Any>?, sentryScope?.getContexts())
    }

    @Test
    fun `collection list value context should be set correctly`() {
        val list = listOf(1, 2, "2", 4)
        val setContext = mapOf("value" to list)
        val expectedContext = mapOf("Context" to setContext)
        sentryScope?.setContext("Context", list)

        assertEquals(expectedContext as Map<String, Any>?, sentryScope?.getContexts())
    }

    @Test
    fun `map value context should be set correctly`() {
        val map = mapOf("test" to "fighter", 1 to "one", "2" to 2)
        val expectedContext = mapOf("Context" to map)
        sentryScope?.setContext("Context", map)

        assertEquals(expectedContext as Map<String, Any>?, sentryScope?.getContexts())
    }

    @Test
    fun `clear scope resets scope to default state`() {
        sentryScope?.level = SentryLevel.WARNING
        sentryScope?.user = User()
        sentryScope?.user?.username = "test"

        sentryScope?.clear()

        assertNull(sentryScope?.user)
        assertNull(sentryScope?.level)
        assertEquals(0, sentryScope?.getContexts()?.size)
        assertEquals(0, sentryScope?.getTags()?.size)
    }
}
