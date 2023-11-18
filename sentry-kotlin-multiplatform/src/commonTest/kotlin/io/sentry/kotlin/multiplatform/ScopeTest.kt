package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.User
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ScopeTest : BaseSentryScopeTest() {

  private val testUsername = "MyUsername"
  private val testEmail = "Email"
  private val testId = "TestId"
  private val testIpAddress = "0.0.1.0"

  private var scope: Scope? = null
  private var user: User? = null

  @BeforeTest
  fun setup() {
    scope = initializeScope()
    user = createTestUser()
  }

  @AfterTest
  fun tearDown() {
    scope = null
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
    scope?.user = user

    assertEquals(user, scope?.user)
  }

  @Test
  fun `modifying user in scope does not persist`() {
    scope?.user = user
    scope?.user?.username = "Test Username"

    assertEquals(testUsername, scope?.user?.username)
  }

  @Test
  fun `adding tags in scope should be set correctly`() {
    scope?.setTag("key", "value")
    scope?.setTag("key2", "value2")

    scope?.getTags()?.let {
      assertEquals(it["key"], "value")
      assertEquals(it["key2"], "value2")
    }
  }

  @Test
  fun `string value context should be set correctly`() {
    val stringContext = mapOf("value" to "Test")
    val expectedContext = mapOf("Context" to stringContext)
    scope?.setContext("Context", "Test")

    assertEquals(expectedContext as Map<String, Any>?, scope?.getContexts())
  }

  @Test
  fun `number value context should be set correctly`() {
    val numberContext = mapOf("value" to 12)
    val expectedContext = mapOf("Context" to numberContext)
    scope?.setContext("Context", 12)

    assertEquals(expectedContext as Map<String, Any>?, scope?.getContexts())
  }

  @Test
  fun `boolean value context should be set correctly`() {
    val booleanContext = mapOf("value" to false)
    val expectedContext = mapOf("Context" to booleanContext)
    scope?.setContext("Context", false)

    assertEquals(expectedContext as Map<String, Any>?, scope?.getContexts())
  }

  @Test
  fun `array value context should be set correctly`() {
    val array = arrayOf(1, 2, 3, "2")
    val arrayContext = mapOf("value" to array)
    val expectedContext = mapOf("Context" to arrayContext)
    scope?.setContext("Context", array)

    assertEquals(expectedContext as Map<String, Any>?, scope?.getContexts())
  }

  @Test
  fun `collection set value context should be set correctly`() {
    val set = setOf(1, 2, "2", 4)
    val setContext = mapOf("value" to set)
    val expectedContext = mapOf("Context" to setContext)
    scope?.setContext("Context", set)

    assertEquals(expectedContext as Map<String, Any>?, scope?.getContexts())
  }

  @Test
  fun `collection list value context should be set correctly`() {
    val list = listOf(1, 2, "2", 4)
    val setContext = mapOf("value" to list)
    val expectedContext = mapOf("Context" to setContext)
    scope?.setContext("Context", list)

    assertEquals(expectedContext as Map<String, Any>?, scope?.getContexts())
  }

  @Test
  fun `map value context should be set correctly`() {
    val map = mapOf("test" to "fighter", 1 to "one", "2" to 2)
    val expectedContext = mapOf("Context" to map)
    scope?.setContext("Context", map)

    assertEquals(expectedContext as Map<String, Any>?, scope?.getContexts())
  }

  @Test
  fun `clear scope resets scope to default state`() {
    scope?.level = SentryLevel.WARNING
    scope?.user = user
    scope?.user?.username = "test"

    scope?.clear()

    assertNull(scope?.user)
    assertNull(scope?.level)
    assertEquals(0, scope?.getContexts()?.size)
    assertEquals(0, scope?.getTags()?.size)
  }
}
