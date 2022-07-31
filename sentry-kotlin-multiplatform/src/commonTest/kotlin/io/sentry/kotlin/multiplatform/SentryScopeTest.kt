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
        sentryScope.setLevel(SentryLevel.WARNING)
        sentryScope.setUser(user)
        sentryScope.setContext("Context", "Fighter")

        val context = HashMap<String, Any>()
        val contextValue = HashMap<Any, Any>()
        contextValue.put("value", "Fighter")
        context.put("Context", contextValue)

        assertEquals(user, sentryScope.getUser())
        assertEquals(SentryLevel.WARNING, sentryScope.getLevel())
        assertEquals(context, sentryScope.getContext())
    }

    @Test
    fun `clear scope resets scope to default state`() {
        val sentryScope = SentryScope()
        initializeScope(sentryScope)
        sentryScope.setLevel(SentryLevel.WARNING)
        sentryScope.setUser(SentryUser())

        sentryScope.clear()

        assertNull(sentryScope.getUser())
        assertNull(sentryScope.getLevel())
        assertEquals(0, sentryScope.getContext()?.size)
    }
}
