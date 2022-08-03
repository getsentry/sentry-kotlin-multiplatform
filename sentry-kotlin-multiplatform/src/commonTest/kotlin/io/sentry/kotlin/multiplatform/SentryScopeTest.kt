package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.SentryUser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SentryScopeTest : BaseSentryScopeTest() {

    @Test
    fun `adding values to scope are properly set`() {
        val sentryScope = initializeScope()
        val user = SentryUser()
        user.username = "TestUsername"
        sentryScope.level = SentryLevel.WARNING
        sentryScope.user = user
        sentryScope.user?.username = "Another"

        val expectedContext = HashMap<String, Any>()
        val contextValue = HashMap<String, Any>()
        contextValue.put("value", "Fighter")
        contextValue.put("Number", 13)
        contextValue.put("Boolean", false)
        expectedContext.put("Context", contextValue)
        sentryScope.setContext("Context", contextValue)

        val tags = HashMap<String, String>()
        tags.put("MyTag", "Value")
        sentryScope.setTag("MyTag", "Value")

        assertEquals(user, sentryScope.user)
        assertEquals(SentryLevel.WARNING, sentryScope.level)
        assertEquals(expectedContext, sentryScope.getContexts())
        assertEquals(tags, sentryScope.getTags())
    }

    @Test
    fun `clear scope resets scope to default state`() {
        val sentryScope = initializeScope()
        sentryScope.level = SentryLevel.WARNING
        sentryScope.user = SentryUser()
        sentryScope.user?.username = "Test Username"

        sentryScope.clear()

        assertNull(sentryScope.user)
        assertNull(sentryScope.level)
        assertEquals(0, sentryScope.getContexts()?.size)
        assertEquals(0, sentryScope.getTags()?.size)
    }
}
