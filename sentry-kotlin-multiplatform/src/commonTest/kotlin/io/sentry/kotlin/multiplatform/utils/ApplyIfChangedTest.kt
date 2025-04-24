package io.sentry.kotlin.multiplatform.utils

import io.sentry.kotlin.multiplatform.util.applyIfChanged
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ApplyIfChangedTest {
    @Test
    fun `applyIfChanged applies change if value changed`() {
        var value = "old value"
        applyIfChanged(value, "new value") {
            value = it
        }
        assertEquals("new value", value)
    }

    @Test
    fun `applyIfChanged does not apply change if value did not change`() {
        applyIfChanged("same value", "same value") {
            // This block should not be executed
            fail("Change was applied even though the value did not change")
        }
    }
}
