package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.utils.fakeDsn
import kotlin.test.Test
import kotlin.test.assertEquals

class DisabledLoggerTest {
    @Test
    fun `native logger skips callback when logs disabled`() {
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
            Sentry.logger.info("disabled log")
            assertEquals(0, callbacks)
        } finally {
            Sentry.close()
        }
    }
}
