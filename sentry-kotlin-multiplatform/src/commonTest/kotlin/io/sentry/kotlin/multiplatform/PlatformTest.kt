package io.sentry.kotlin.multiplatform

import kotlin.test.Test
import kotlin.test.assertEquals

class PlatformTest : BaseSentryTest() {
    private val fakeDsn = "https://abc@def.ingest.sentry.io/1234567"

    @Test
    fun `initialize platforms`() {
        SentryKMP.start(context) {
            it.dsn = fakeDsn
        }
        SentryKMP.close()
    }

    @Test
    fun `initial values are set correctly`() {
        SentryKMP.start(context) {
            it.dsn = fakeDsn

            // this can be further extended
            assertEquals(it.debug, false)
        }
        SentryKMP.close()
    }
}