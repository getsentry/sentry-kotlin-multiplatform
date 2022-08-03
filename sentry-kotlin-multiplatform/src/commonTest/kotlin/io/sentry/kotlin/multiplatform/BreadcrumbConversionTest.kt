package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BreadcrumbConversionTest {
    private lateinit var converter: BreadcrumbConverter
    private lateinit var breadcrumb: Breadcrumb

    @BeforeTest
    fun setup() {
        breadcrumb = Breadcrumb()
        converter = BreadcrumbConverter(breadcrumb)
    }

    @Test
    fun `setData and convert SentryBreadcrumb to Platform Breadcrumb has proper values`() {
        breadcrumb.setData("key", "value")

        assertEquals(mapOf("key" to "value"), converter.getData() as Map<String, Any>)
    }

    @Test
    fun `setLevel and convert SentryBreadcrumb to Platform Breadcrumb has proper values`() {
        breadcrumb.setLevel(SentryLevel.ERROR)

        assertEquals(SentryLevel.ERROR, converter.getLevel())
    }

    @Test
    fun `setMessage and convert SentryBreadcrumb to Platform Breadcrumb has proper values`() {
        breadcrumb.setMessage("TestMessage")

        assertEquals("TestMessage", converter.getMessage())
    }

    @Test
    fun `setType and convert SentryBreadcrumb to Platform Breadcrumb has proper values`() {
        breadcrumb.setType("TestType")

        assertEquals("TestType", converter.getType())
    }

    @Test
    fun `setCategory and convert SentryBreadcrumb to Platform Breadcrumb has proper values`() {
        breadcrumb.setCategory("TestCategory")

        assertEquals("TestCategory", converter.getCategory())
    }
}
