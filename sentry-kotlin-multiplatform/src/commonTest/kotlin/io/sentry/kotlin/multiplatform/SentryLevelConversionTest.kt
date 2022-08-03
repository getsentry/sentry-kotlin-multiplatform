package io.sentry.kotlin.multiplatform

import kotlin.test.Test
import kotlin.test.assertEquals

class SentryLevelConversionTest {

    private var converter: SentryLevelConverter? = null

    @Test
    fun `convert SentryLevel debug to Platform Sentry Level has proper value`() {
        converter = SentryLevelConverter(SentryLevel.DEBUG)

        assertEquals(SentryLevelNumConstants.DEBUG_LEVEL, converter?.getLevel())
    }

    @Test
    fun `convert SentryLevel info to Platform Sentry Level has proper value`() {
        converter = SentryLevelConverter(SentryLevel.INFO)

        assertEquals(SentryLevelNumConstants.INFO_LEVEL, converter?.getLevel())
    }

    @Test
    fun `convert SentryLevel warning to Platform Sentry Level has proper value`() {
        converter = SentryLevelConverter(SentryLevel.WARNING)

        assertEquals(SentryLevelNumConstants.WARNING_LEVEL, converter?.getLevel())
    }

    @Test
    fun `convert SentryLevel error to Platform Sentry Level has proper value`() {
        converter = SentryLevelConverter(SentryLevel.ERROR)

        assertEquals(SentryLevelNumConstants.ERROR_LEVEL, converter?.getLevel())
    }

    @Test
    fun `convert SentryLevel fatal to Platform Sentry Level has proper value`() {
        converter = SentryLevelConverter(SentryLevel.FATAL)

        assertEquals(SentryLevelNumConstants.FATAL_LEVEL, converter?.getLevel())
    }
}
