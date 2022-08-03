package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.User
import kotlin.test.*

class SentryScopeTest : BaseSentryScopeTest() {

    private var sentryScope: SentryScope? = null

    @BeforeTest
    fun setup() {
        sentryScope = initializeScope()
    }

    @AfterTest
    fun tearDown() {
        sentryScope = null
    }

    @Test
    fun `adding values to scope are properly set`() {
        val user = User()
        user.username = "TestUsername"
        sentryScope?.level = SentryLevel.WARNING
        sentryScope?.user = user

        val expectedContext = HashMap<String, Any>()
        val contextValue = HashMap<String, Any>()
        contextValue.put("value", "Fighter")
        contextValue.put("Number", 13)
        contextValue.put("Boolean", false)

        expectedContext.put("Context", contextValue)
        sentryScope?.setContext("Context", contextValue)

        val tags = HashMap<String, String>()
        tags.put("MyTag", "Value")
        sentryScope?.setTag("MyTag", "Value")

        assertEquals(user.username, sentryScope?.user?.username)
        assertEquals(SentryLevel.WARNING, sentryScope?.level)
        assertEquals(expectedContext, sentryScope?.getContexts())
        assertEquals(tags, sentryScope?.getTags())
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
        val array = arrayOf(1, 2, 3, 4)
        val arrayContext = mapOf("value" to array)
        val expectedContext = mapOf("Context" to arrayContext)
        sentryScope?.setContext("Context", arrayContext)

        assertEquals(expectedContext as Map<String, Any>?, sentryScope?.getContexts())
    }

    @Test
    fun `collection value context should be set correctly`() {
        val set = setOf(1, 2, 3, 4)
        val setContext = mapOf("value" to set)
        val expectedContext = mapOf("Context" to setContext)
        sentryScope?.setContext("Context", setContext)

        assertEquals(expectedContext as Map<String, Any>?, sentryScope?.getContexts())
    }

    @Test
    fun `clear scope resets scope to default state`() {
        sentryScope?.level = SentryLevel.WARNING
        sentryScope?.user = User()
        sentryScope?.user?.username = "Test Username"

        sentryScope?.clear()

        assertNull(sentryScope?.user)
        assertNull(sentryScope?.level)
        assertEquals(0, sentryScope?.getContexts()?.size)
        assertEquals(0, sentryScope?.getTags()?.size)
    }
}
