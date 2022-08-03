package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toCocoaBreadcrumb
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CocoaSentryBreadcrumbTest {

    @Test
    fun `convert SentryBreadcrumb to CocoaBreadcrumb has proper values`() {
        val sentryBreadcrumb = Breadcrumb.debug("test")
        sentryBreadcrumb.setData("my test", "value")

        val cocoaBreadcrumb = sentryBreadcrumb.toCocoaBreadcrumb()

        assertEquals(sentryBreadcrumb.getData() as Map<Any?, Any>?, cocoaBreadcrumb.data())
        assertEquals(sentryBreadcrumb.getLevel(), cocoaBreadcrumb.level.toKmpSentryLevel())
        assertEquals(sentryBreadcrumb.getType(), cocoaBreadcrumb.type)
        assertEquals(sentryBreadcrumb.getMessage(), cocoaBreadcrumb.message)
        assertEquals(sentryBreadcrumb.getCategory(), cocoaBreadcrumb.category)
    }

    @Test
    fun `testing me`() {
        val breadcrumb = Breadcrumb.debug("test")
        breadcrumb.setMessage("test")

        val cocoaBreadcrumb = breadcrumb.toCocoaBreadcrumb()
        cocoaBreadcrumb.setMessage("nothing")

        assertNotEquals(breadcrumb.getMessage(), cocoaBreadcrumb.message)
    }
}
