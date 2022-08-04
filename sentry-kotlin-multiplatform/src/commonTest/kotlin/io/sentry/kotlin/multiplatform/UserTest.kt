package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.User
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UserTest {
    private var user = User()

    private fun createTestUser(): User {
        return User().apply {
            username = "TestUsername"
            email = "TestEmail"
            id = "TestId"
            ipAddress = "192.168.0.1"
            unknown = mutableMapOf("key" to "value", "key2" to 12)
            other = mutableMapOf("key" to "value")
        }
    }

    @BeforeTest
    fun setup() {
        user = User()
    }

    @Test
    fun `fromMap with empty username value sets empty string`() {
        val map = mapOf<String, String>()
        user = User.fromMap(map)

        assertEquals("", user.username)
    }

    @Test
    fun `fromMap with empty email value sets empty string`() {
        val map = mapOf<String, String>()
        user = User.fromMap(map)

        assertEquals("", user.email)
    }

    @Test
    fun `fromMap with empty id value sets empty string`() {
        val map = mapOf<String, String>()
        user = User.fromMap(map)

        assertEquals("", user.id)
    }

    @Test
    fun `fromMap with empty ip address value sets null`() {
        val map = mapOf<String, String>()
        user = User.fromMap(map)

        assertEquals(null, user.ipAddress)
    }

    @Test
    fun `initializing with another user sets value properly`() {
        val anotherUser = createTestUser()
        user = User(anotherUser)

        assertEquals(anotherUser, user)
    }
}
