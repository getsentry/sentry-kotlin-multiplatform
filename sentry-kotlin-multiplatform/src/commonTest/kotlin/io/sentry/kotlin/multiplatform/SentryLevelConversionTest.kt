package io.sentry.kotlin.multiplatform

import kotlin.test.Test
import kotlin.test.assertEquals

class SentryLevelConversionTest {

    private var converter: SentryLevelConverter? = null

    @Test
    fun `convert SentryLevel debug to Platform Sentry Level has proper value`() {
        converter = SentryLevelConverter(SentryLevel.DEBUG)

        assertEquals(SentryLevel.DEBUG, converter?.getLevel())
    }

    @Test
    fun `convert SentryLevel info to Platform Sentry Level has proper value`() {
        converter = SentryLevelConverter(SentryLevel.INFO)

        assertEquals(SentryLevel.INFO, converter?.getLevel())
    }

    @Test
    fun `convert SentryLevel warning to Platform Sentry Level has proper value`() {
        converter = SentryLevelConverter(SentryLevel.WARNING)

        assertEquals(SentryLevel.WARNING, converter?.getLevel())
    }

    @Test
    fun `convert SentryLevel error to Platform Sentry Level has proper value`() {
        converter = SentryLevelConverter(SentryLevel.ERROR)

        assertEquals(SentryLevel.ERROR, converter?.getLevel())
    }

    @Test
    fun `convert SentryLevel fatal to Platform Sentry Level has proper value`() {
        converter = SentryLevelConverter(SentryLevel.FATAL)

        assertEquals(SentryLevel.FATAL, converter?.getLevel())
    }
}
