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

        syncFields(sentryScope)

        assertEquals(user, sentryScope.user)
        assertEquals(SentryLevel.WARNING, sentryScope.level)
        assertEquals(expectedContext, sentryScope.contexts)
        assertEquals(tags, sentryScope.tags)
    }

    @Test
    fun `clear scope resets scope to default state`() {
        val sentryScope = SentryScope()
        initializeScope(sentryScope)
        sentryScope.level = SentryLevel.WARNING
        sentryScope.user = SentryUser()
        sentryScope.user?.username = "Test Username"

        syncFields(sentryScope)
        sentryScope.clear()

        assertNull(sentryScope.user)
        assertNull(sentryScope.level)
        //assertEquals(0, sentryScope.contexts?.size)
    }
}
