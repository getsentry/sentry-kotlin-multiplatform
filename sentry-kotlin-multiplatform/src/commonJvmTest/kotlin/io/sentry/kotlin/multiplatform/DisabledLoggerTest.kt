package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.utils.fakeDsn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DisabledLoggerTest : BaseSentryTest() {
    @Test
    fun `native logger invokes callback for manual logs when automatic logs disabled`() {
        var callbacks = 0
        try {
            Sentry.init {
                it.dsn = fakeDsn
                it.logs.enabled = false
                it.logs.beforeSend = {
                    callbacks++
                    null
                }
            }
            assertTrue(Sentry.isEnabled())
            Sentry.logger.info("manual log")
            assertEquals(1, callbacks)
        } finally {
            Sentry.close()
        }
    }
}
