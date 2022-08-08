package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BreadcrumbConversionTest {
    private lateinit var converter: BreadcrumbTestConverter
    private lateinit var breadcrumb: Breadcrumb

    @BeforeTest
    fun setup() {
        breadcrumb = Breadcrumb()
        converter = BreadcrumbTestConverter(breadcrumb)
    }

    @JsName("When_ConvertingData_Then_HasProperValues")
    @Test
    fun `setData and convert SentryBreadcrumb to Platform Breadcrumb has proper values`() {
        breadcrumb.setData("key", "value")

        assertEquals(mapOf("key" to "value"), converter.getData() as Map<String, Any>)
    }

    @JsName("When_ConvertingLevel_Then_HasProperValues")
    @Test
    fun `setLevel and convert SentryBreadcrumb to Platform Breadcrumb has proper values`() {
        breadcrumb.level = SentryLevel.ERROR

        assertEquals(SentryLevel.ERROR, converter.getLevel())
    }


    @JsName("When_ConvertingMessage_Then_HasProperValues")
    @Test
    fun `setMessage and convert SentryBreadcrumb to Platform Breadcrumb has proper values`() {
        breadcrumb.message = "TestMessage"

        assertEquals("TestMessage", converter.getMessage())
    }

    @JsName("When_ConvertingType_Then_HasProperValues")
    @Test
    fun `setType and convert SentryBreadcrumb to Platform Breadcrumb has proper values`() {
        breadcrumb.type = "TestType"

        assertEquals("TestType", converter.getType())
    }

    @JsName("When_ConvertingCategory_Then_HasProperValues")
    @Test
    fun `setCategory and convert SentryBreadcrumb to Platform Breadcrumb has proper values`() {
        breadcrumb.category = "TestCategory"

        assertEquals("TestCategory", converter.getCategory())
    }
}
