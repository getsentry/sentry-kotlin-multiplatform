package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.SentryId
import kotlin.test.Test
import kotlin.test.assertEquals

class AppleSentryIdTest {

  @Test
  fun `Cocoa SentryId with invalid uuid string returns only zeroes`() {
    val uuidString = "ec720-b6f6-4efc--5c1"
    val expected = SentryId.EMPTY_ID.toString()
    val actual = SentryId(uuidString).toString()
    assertEquals(expected, actual)
  }
}
