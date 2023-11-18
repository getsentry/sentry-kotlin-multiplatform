package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
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

  @Test
  fun `setData and convert SentryBreadcrumb to Platform Breadcrumb has proper values`() {
    breadcrumb.setData("key", "value")

    assertEquals(mapOf("key" to "value"), converter.getData() as Map<String, Any>)
  }

  @Test
  fun `setLevel and convert SentryBreadcrumb to Platform Breadcrumb has proper values`() {
    breadcrumb.level = SentryLevel.ERROR

    assertEquals(SentryLevel.ERROR, converter.getLevel())
  }

  @Test
  fun `setMessage and convert SentryBreadcrumb to Platform Breadcrumb has proper values`() {
    breadcrumb.message = "TestMessage"

    assertEquals("TestMessage", converter.getMessage())
  }

  @Test
  fun `setType and convert SentryBreadcrumb to Platform Breadcrumb has proper values`() {
    breadcrumb.type = "TestType"

    assertEquals("TestType", converter.getType())
  }

  @Test
  fun `setCategory and convert SentryBreadcrumb to Platform Breadcrumb has proper values`() {
    breadcrumb.category = "TestCategory"

    assertEquals("TestCategory", converter.getCategory())
  }
}
