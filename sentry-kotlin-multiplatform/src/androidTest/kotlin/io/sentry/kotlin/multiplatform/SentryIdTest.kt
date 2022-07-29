package io.sentry.kotlin.multiplatform

import kotlin.test.Test
import kotlin.test.assertFailsWith

class AndroidSentryIdTest {

    @Test
    fun `Android SentryId with invalid uuid string throws IllegalArgumentException`() {
        val uuidString = "ec720-b6f6-4efc--5c1"
        assertFailsWith(IllegalArgumentException::class) {
            SentryId(uuidString)
        }
    }
}
