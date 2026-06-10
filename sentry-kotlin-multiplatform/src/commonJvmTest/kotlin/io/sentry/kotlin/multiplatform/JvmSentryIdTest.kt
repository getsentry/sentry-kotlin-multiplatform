package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.SentryId
import kotlin.test.Test
import kotlin.test.assertFailsWith

class JvmSentryIdTest {

    @Test
    fun `Jvm SentryId with invalid uuid string throws IllegalArgumentException`() {
        val uuidString = "ec720-b6f6-4efc--5c1"
        assertFailsWith(IllegalArgumentException::class) {
            SentryId(uuidString)
        }
    }
}
