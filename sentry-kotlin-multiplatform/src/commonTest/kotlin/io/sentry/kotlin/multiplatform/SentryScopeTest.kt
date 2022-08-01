package io.sentry.kotlin.multiplatform

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SentryScopeTest : BaseSentryScopeTest() {

    @Test
    fun `adding values to scope are properly set`() {
        val sentryScope = SentryScope()
        initializeScope(sentryScope)
        val user = SentryUser()
        sentryScope.level = SentryLevel.WARNING
        sentryScope.user = user
        sentryScope.user?.email = "test@email.com"
        user.email = "test@email.com"

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

        assertEquals(user.id, sentryScope.user?.id)
        assertEquals(user.username, sentryScope.user?.username)
        assertEquals(user.ipAddress.toString(), sentryScope.user?.ipAddress.toString())
        assertEquals(user.email, sentryScope.user?.email)
        assertEquals(SentryLevel.WARNING, sentryScope.level)
        assertEquals(expectedContext, sentryScope.getContexts())
        assertEquals(tags, sentryScope.getTags())
    }

    @Test
    fun `clear scope resets scope to default state`() {
        val sentryScope = SentryScope()
        initializeScope(sentryScope)
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
