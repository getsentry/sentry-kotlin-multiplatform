package io.sentry.kotlin.multiplatform

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SentryScopeTest : BaseSentryScopeTest() {

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
