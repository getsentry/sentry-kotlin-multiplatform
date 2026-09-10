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
    fun `initializing with another user sets value properly`() {
        val anotherUser = createTestUser()
        user = User(anotherUser)

        assertEquals(anotherUser, user)
    }
}
