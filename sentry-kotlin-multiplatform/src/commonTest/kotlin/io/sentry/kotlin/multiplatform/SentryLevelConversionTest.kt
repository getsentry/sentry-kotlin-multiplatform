package io.sentry.kotlin.multiplatform

import kotlin.test.Test
import kotlin.test.assertEquals

class SentryLevelConversionTest {

    private var converter: SentryLevelConverter? = SentryLevelConverter()

    @Test
    fun `convert SentryLevel debug to Platform Sentry Level has proper value`() {
        assertEquals(SentryLevel.DEBUG, converter?.convert(SentryLevel.DEBUG))
    }

    @Test
    fun `convert SentryLevel info to Platform Sentry Level has proper value`() {

        assertEquals(SentryLevel.INFO, converter?.convert(SentryLevel.INFO))
    }

    @Test
    fun `convert SentryLevel warning to Platform Sentry Level has proper value`() {

        assertEquals(SentryLevel.WARNING, converter?.convert(SentryLevel.WARNING))
    }

    @Test
    fun `convert SentryLevel error to Platform Sentry Level has proper value`() {

        assertEquals(SentryLevel.ERROR, converter?.convert(SentryLevel.ERROR))
    }

    @Test
    fun `convert SentryLevel fatal to Platform Sentry Level has proper value`() {

        assertEquals(SentryLevel.FATAL, converter?.convert(SentryLevel.FATAL))
    }
}
