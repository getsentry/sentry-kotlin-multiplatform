package io.sentry.kotlin.multiplatform

import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals

class SentryLevelConversionTest {

    private var converter: SentryLevelTestConverter? = SentryLevelTestConverter()

    @JsName("When_ConvertingNullToPlatformLevel_Then_HasProperValues")
    @Test
    fun `convert SentryLevel null to Platform SentryLevel is null`() {

        assertEquals(null, converter?.convert(null))
    }

    @JsName("When_ConvertingDebugToPlatformLevel_Then_HasProperValues")
    @Test
    fun `convert SentryLevel debug to Platform SentryLevel has proper value`() {

        assertEquals(SentryLevel.DEBUG, converter?.convert(SentryLevel.DEBUG))
    }

    @JsName("When_ConvertingInfoToPlatformLevel_Then_HasProperValues")
    @Test
    fun `convert SentryLevel info to Platform SentryLevel has proper value`() {

        assertEquals(SentryLevel.INFO, converter?.convert(SentryLevel.INFO))
    }

    @JsName("When_ConvertingWarningToPlatformLevel_Then_HasProperValues")
    @Test
    fun `convert SentryLevel warning to Platform SentryLevel has proper value`() {

        assertEquals(SentryLevel.WARNING, converter?.convert(SentryLevel.WARNING))
    }

    @JsName("When_ConvertingErrorToPlatformLevel_Then_HasProperValues")
    @Test
    fun `convert SentryLevel error to Platform SentryLevel has proper value`() {

        assertEquals(SentryLevel.ERROR, converter?.convert(SentryLevel.ERROR))
    }

    @JsName("When_ConvertingFatalToPlatformLevel_Then_HasProperValues")
    @Test
    fun `convert SentryLevel fatal to Platform SentryLevel has proper value`() {

        assertEquals(SentryLevel.FATAL, converter?.convert(SentryLevel.FATAL))
    }
}
