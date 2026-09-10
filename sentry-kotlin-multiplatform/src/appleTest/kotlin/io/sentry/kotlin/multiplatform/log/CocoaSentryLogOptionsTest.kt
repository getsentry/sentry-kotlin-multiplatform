package io.sentry.kotlin.multiplatform.log

import cocoapods.Sentry.SentryAttribute
import io.sentry.kotlin.multiplatform.CocoaSentryOptions
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.extensions.applyCocoaBaseOptions
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import cocoapods.Sentry.SentryLog as CocoaSentryLog

class CocoaSentryLogOptionsTest {
    @Test
    fun `logs enablement is applied to the top level Cocoa option`() {
        val cocoaOptions = CocoaSentryOptions()
        val options = SentryOptions()

        cocoaOptions.applyCocoaBaseOptions(options)
        assertFalse(cocoaOptions.enableLogs())

        options.logs.enabled = true
        cocoaOptions.applyCocoaBaseOptions(options)
        assertTrue(cocoaOptions.enableLogs())

        options.logs.enabled = false
        cocoaOptions.applyCocoaBaseOptions(options)
        assertFalse(cocoaOptions.enableLogs())
    }

    @Test
    fun `before send can drop a native log`() {
        val options = SentryOptions().apply { logs.beforeSend = { null } }
        val cocoaOptions = CocoaSentryOptions().apply { applyCocoaBaseOptions(options) }
        val callback = assertNotNull(cocoaOptions.beforeSendLog())

        assertNull(callback(CocoaSentryLog(SentryLogLevel.INFO.toCocoaSentryLogLevel(), "drop me")))
        assertNull(callback(null))
    }

    @Test
    fun `before send preserves typed attributes and applies mutations and deletions`() {
        val nativeLog = CocoaSentryLog(SentryLogLevel.INFO.toCocoaSentryLogLevel(), "original")
        nativeLog.setAttribute(SentryAttribute(string = "old"), forKey = "text")
        nativeLog.setAttribute(SentryAttribute(boolean = true), forKey = "enabled")
        nativeLog.setAttribute(SentryAttribute(integer = 42), forKey = "count")
        nativeLog.setAttribute(SentryAttribute(double = 1.5), forKey = "ratio")
        nativeLog.setAttribute(SentryAttribute(string = "remove"), forKey = "deleted")
        val options =
            SentryOptions().apply {
                logs.beforeSend = { log ->
                    assertEquals("old", log.attributes["text"]?.stringOrNull)
                    assertEquals(true, log.attributes["enabled"]?.booleanOrNull)
                    assertEquals(42L, log.attributes["count"]?.longOrNull)
                    assertEquals(1.5, log.attributes["ratio"]?.doubleOrNull)
                    log.body = "updated"
                    log.level = SentryLogLevel.ERROR
                    log.severityNumber = 17
                    log.attributes["text"] = "new"
                    log.attributes["enabled"] = false
                    log.attributes["count"] = 43L
                    log.attributes["ratio"] = 2.5
                    log.attributes["added"] = "new attribute"
                    log.attributes.remove("deleted")
                    log
                }
            }
        val cocoaOptions = CocoaSentryOptions().apply { applyCocoaBaseOptions(options) }

        val result = assertNotNull(cocoaOptions.beforeSendLog())(nativeLog)

        assertEquals(nativeLog, result)
        assertEquals("updated", nativeLog.body())
        assertEquals(SentryLogLevel.ERROR.toCocoaSentryLogLevel(), nativeLog.level())
        assertEquals(17, nativeLog.severityNumber()?.intValue)
        assertFalse(nativeLog.attributes().containsKey("deleted"))
        assertTrue(nativeLog.attributes().values.all { it is SentryAttribute })
        val attributes = nativeLog.toKmpSentryLog().attributes
        assertEquals("new", attributes["text"]?.stringOrNull)
        assertEquals(false, attributes["enabled"]?.booleanOrNull)
        assertEquals(43L, attributes["count"]?.longOrNull)
        assertEquals(2.5, attributes["ratio"]?.doubleOrNull)
        assertEquals("new attribute", attributes["added"]?.stringOrNull)
    }
}
