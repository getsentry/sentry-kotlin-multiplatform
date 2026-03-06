package io.sentry.kotlin.multiplatform.log

import io.sentry.kotlin.multiplatform.SentryAttributes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Tests for [DefaultSentryLogBuilder] DSL builder. */
class SentryLogBuilderTest {
    @Test
    fun `message with body sets plain template`() {
        val builder = DefaultSentryLogBuilder()

        builder.message("Hello world")

        assertEquals("Hello world", builder.template)
        assertTrue(builder.args.isEmpty())
    }

    @Test
    fun `message with template and args sets both`() {
        val builder = DefaultSentryLogBuilder()

        builder.message("User %s logged in from %s", "alice", "192.168.1.1")

        assertEquals("User %s logged in from %s", builder.template)
        assertEquals(2, builder.args.size)
        assertEquals("alice", builder.args[0])
        assertEquals("192.168.1.1", builder.args[1])
    }

    @Test
    fun `message with body clears previous args`() {
        val builder = DefaultSentryLogBuilder()
        builder.message("Template %s", "arg1")

        builder.message("Plain message")

        assertEquals("Plain message", builder.template)
        assertTrue(builder.args.isEmpty())
    }

    @Test
    fun `message called multiple times uses last value`() {
        val builder = DefaultSentryLogBuilder()

        builder.message("First")
        builder.message("Second")
        builder.message("Third")

        assertEquals("Third", builder.template)
    }

    @Test
    fun `template is null by default`() {
        val builder = DefaultSentryLogBuilder()

        assertNull(builder.template)
    }

    @Test
    fun `args is empty by default`() {
        val builder = DefaultSentryLogBuilder()

        assertTrue(builder.args.isEmpty())
    }

    @Test
    fun `attributes with prebuilt merges into builder`() {
        val builder = DefaultSentryLogBuilder()
        val prebuilt = SentryAttributes.empty()
        prebuilt["key1"] = "value1"
        prebuilt["key2"] = 42

        builder.attributes(prebuilt)

        assertEquals("value1", builder.customAttributes["key1"]?.stringOrNull)
        assertEquals(42L, builder.customAttributes["key2"]?.longOrNull)
    }

    @Test
    fun `attributes with block allows inline definition`() {
        val builder = DefaultSentryLogBuilder()

        builder.attributes {
            this["user.id"] = "user-123"
            this["request.duration"] = 150
        }

        assertEquals("user-123", builder.customAttributes["user.id"]?.stringOrNull)
        assertEquals(150L, builder.customAttributes["request.duration"]?.longOrNull)
    }

    @Test
    fun `multiple attributes calls merge not replace`() {
        val builder = DefaultSentryLogBuilder()

        builder.attributes {
            this["key1"] = "value1"
        }
        builder.attributes {
            this["key2"] = "value2"
        }

        assertEquals("value1", builder.customAttributes["key1"]?.stringOrNull)
        assertEquals("value2", builder.customAttributes["key2"]?.stringOrNull)
    }

    @Test
    fun `attributes with same key uses last value`() {
        val builder = DefaultSentryLogBuilder()

        builder.attributes {
            this["key"] = "first"
        }
        builder.attributes {
            this["key"] = "second"
        }

        assertEquals("second", builder.customAttributes["key"]?.stringOrNull)
    }

    @Test
    fun `message with null arg preserves null in args array`() {
        val builder = DefaultSentryLogBuilder()

        builder.message("Value is %s", null)

        assertEquals(1, builder.args.size)
        assertNull(builder.args[0])
    }

    @Test
    fun `customAttributes is empty by default`() {
        val builder = DefaultSentryLogBuilder()

        assertTrue(builder.customAttributes.isEmpty())
    }

    @Test
    fun `prebuilt and block attributes can be combined`() {
        val builder = DefaultSentryLogBuilder()
        val prebuilt = SentryAttributes.empty()
        prebuilt["prebuilt"] = "yes"

        builder.attributes(prebuilt)
        builder.attributes {
            this["inline"] = "also yes"
        }

        assertEquals("yes", builder.customAttributes["prebuilt"]?.stringOrNull)
        assertEquals("also yes", builder.customAttributes["inline"]?.stringOrNull)
    }

    // ========================
    // Formatted output tests
    // ========================

    @Test
    fun `buildFormatted returns null when no message set`() {
        val builder = DefaultSentryLogBuilder()

        val result = builder.buildFormatted()

        assertNull(result)
    }

    @Test
    fun `buildFormatted with plain message returns body unchanged`() {
        val builder = DefaultSentryLogBuilder()
        builder.message("Hello world")

        val result = builder.buildFormatted()

        assertEquals("Hello world", result?.body)
        assertTrue(result?.attributes?.isEmpty() == true)
    }

    @Test
    fun `buildFormatted substitutes args into template`() {
        val builder = DefaultSentryLogBuilder()
        builder.message("User %s logged in from %s", "alice", "192.168.1.1")

        val result = builder.buildFormatted()

        assertEquals("User alice logged in from 192.168.1.1", result?.body)
    }

    @Test
    fun `buildFormatted adds template info to attributes`() {
        val builder = DefaultSentryLogBuilder()
        builder.message("User %s logged in", "bob")

        val result = builder.buildFormatted()

        assertEquals("User %s logged in", result?.attributes?.get("sentry.message.template")?.stringOrNull)
        assertEquals("bob", result?.attributes?.get("sentry.message.parameter.0")?.stringOrNull)
    }

    @Test
    fun `buildFormatted includes custom attributes`() {
        val builder = DefaultSentryLogBuilder()
        builder.message("Test message")
        builder.attributes {
            this["custom.key"] = "custom.value"
        }

        val result = builder.buildFormatted()

        assertEquals("custom.value", result?.attributes?.get("custom.key")?.stringOrNull)
    }

    @Test
    fun `buildFormatted merges template info and custom attributes`() {
        val builder = DefaultSentryLogBuilder()
        builder.message("Hello %s", "world")
        builder.attributes {
            this["env"] = "prod"
        }

        val result = builder.buildFormatted()

        assertEquals("Hello world", result?.body)
        assertEquals("Hello %s", result?.attributes?.get("sentry.message.template")?.stringOrNull)
        assertEquals("world", result?.attributes?.get("sentry.message.parameter.0")?.stringOrNull)
        assertEquals("prod", result?.attributes?.get("env")?.stringOrNull)
    }

    @Test
    fun `buildFormatted handles null args as string null`() {
        val builder = DefaultSentryLogBuilder()
        builder.message("Value is %s", null)

        val result = builder.buildFormatted()

        assertEquals("Value is null", result?.body)
        assertEquals("null", result?.attributes?.get("sentry.message.parameter.0")?.stringOrNull)
    }

    @Test
    fun `buildFormatted handles double percent as literal percent`() {
        val builder = DefaultSentryLogBuilder()
        builder.message("Progress: %s%% complete", 50)

        val result = builder.buildFormatted()

        assertEquals("Progress: 50% complete", result?.body)
    }

    @Test
    fun `buildFormatted handles more placeholders than args`() {
        val builder = DefaultSentryLogBuilder()
        builder.message("A %s B %s C %s", "one", "two")

        val result = builder.buildFormatted()

        assertEquals("A one B two C %s", result?.body)
    }

    @Test
    fun `buildFormatted handles no placeholders with args`() {
        val builder = DefaultSentryLogBuilder()
        builder.message("No placeholders", "unused")

        val result = builder.buildFormatted()

        assertEquals("No placeholders", result?.body)
        // Args still captured in attributes
        assertEquals("No placeholders", result?.attributes?.get("sentry.message.template")?.stringOrNull)
        assertEquals("unused", result?.attributes?.get("sentry.message.parameter.0")?.stringOrNull)
    }

    @Test
    fun `buildFormatted handles empty string message`() {
        val builder = DefaultSentryLogBuilder()
        builder.message("")

        val result = builder.buildFormatted()

        assertEquals("", result?.body)
        assertTrue(result?.attributes?.isEmpty() == true)
    }

    @Test
    fun `buildFormatted handles empty string with args`() {
        val builder = DefaultSentryLogBuilder()
        builder.message("", "arg")

        val result = builder.buildFormatted()

        assertEquals("", result?.body)
        assertEquals("", result?.attributes?.get("sentry.message.template")?.stringOrNull)
        assertEquals("arg", result?.attributes?.get("sentry.message.parameter.0")?.stringOrNull)
    }

    @Test
    fun `buildFormatted handles unicode characters`() {
        val builder = DefaultSentryLogBuilder()
        builder.message("Hello %s! 你好 🎉", "世界")

        val result = builder.buildFormatted()

        assertEquals("Hello 世界! 你好 🎉", result?.body)
    }

    @Test
    fun `buildFormatted handles unicode in attributes`() {
        val builder = DefaultSentryLogBuilder()
        builder.message("test")
        builder.attributes {
            this["emoji"] = "🚀"
            this["chinese"] = "中文"
        }

        val result = builder.buildFormatted()

        assertEquals("🚀", result?.attributes?.get("emoji")?.stringOrNull)
        assertEquals("中文", result?.attributes?.get("chinese")?.stringOrNull)
    }

    @Test
    fun `buildFormatted handles very long message`() {
        val builder = DefaultSentryLogBuilder()
        val longMessage = "x".repeat(10000)
        builder.message(longMessage)

        val result = builder.buildFormatted()

        assertEquals(longMessage, result?.body)
        assertEquals(10000, result?.body?.length)
    }

    @Test
    fun `buildFormatted handles very long arg`() {
        val builder = DefaultSentryLogBuilder()
        val longArg = "y".repeat(10000)
        builder.message("Value: %s", longArg)

        val result = builder.buildFormatted()

        assertEquals("Value: $longArg", result?.body)
        assertEquals(longArg, result?.attributes?.get("sentry.message.parameter.0")?.stringOrNull)
    }

    @Test
    fun `buildFormatted handles special characters in message`() {
        val builder = DefaultSentryLogBuilder()
        builder.message("Special: \t\n\r\\\"'<>&")

        val result = builder.buildFormatted()

        assertEquals("Special: \t\n\r\\\"'<>&", result?.body)
    }

    @Test
    fun `buildFormatted handles special characters in args`() {
        val builder = DefaultSentryLogBuilder()
        builder.message("Value: %s", "\t\n\r\\\"'")

        val result = builder.buildFormatted()

        assertEquals("Value: \t\n\r\\\"'", result?.body)
    }

    @Test
    fun `buildFormatted handles multiple consecutive placeholders`() {
        val builder = DefaultSentryLogBuilder()
        builder.message("%s%s%s", "a", "b", "c")

        val result = builder.buildFormatted()

        assertEquals("abc", result?.body)
    }

    @Test
    fun `buildFormatted handles multiple consecutive percent signs`() {
        val builder = DefaultSentryLogBuilder()
        builder.message("100%% complete, %%%% done")

        val result = builder.buildFormatted()

        assertEquals("100% complete, %% done", result?.body)
    }

    @Test
    fun `buildFormatted handles placeholder at start`() {
        val builder = DefaultSentryLogBuilder()
        builder.message("%s is the value", "42")

        val result = builder.buildFormatted()

        assertEquals("42 is the value", result?.body)
    }

    @Test
    fun `buildFormatted handles placeholder at end`() {
        val builder = DefaultSentryLogBuilder()
        builder.message("The value is %s", "42")

        val result = builder.buildFormatted()

        assertEquals("The value is 42", result?.body)
    }

    @Test
    fun `buildFormatted handles only placeholder`() {
        val builder = DefaultSentryLogBuilder()
        builder.message("%s", "hello")

        val result = builder.buildFormatted()

        assertEquals("hello", result?.body)
    }

    @Test
    fun `buildFormatted handles numeric args`() {
        val builder = DefaultSentryLogBuilder()
        builder.message("Int: %s, Long: %s, Double: %s", 42, 123456789L, 3.14159)

        val result = builder.buildFormatted()

        assertEquals("Int: 42, Long: 123456789, Double: 3.14159", result?.body)
    }

    @Test
    fun `buildFormatted handles boolean args`() {
        val builder = DefaultSentryLogBuilder()
        builder.message("Enabled: %s, Disabled: %s", true, false)

        val result = builder.buildFormatted()

        assertEquals("Enabled: true, Disabled: false", result?.body)
    }

    @Test
    fun `buildFormatted handles mixed null and non-null args`() {
        val builder = DefaultSentryLogBuilder()
        builder.message("A: %s, B: %s, C: %s", "value", null, "other")

        val result = builder.buildFormatted()

        assertEquals("A: value, B: null, C: other", result?.body)
    }
}
