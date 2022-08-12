package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toCocoaBreadcrumb
import io.sentry.kotlin.multiplatform.extensions.toSentryBreadcrumb
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class BreadcrumbAppleTest {

    @Test
    fun `Cocoa Breadcrumb can be modified via callback and should return the modified Breadcrumb`() {
        val expectedCocoaBreadcrumb = CocoaBreadcrumb().apply {
            message = "changed message"
            type = "test type"
            category = "test category"
            setData(mapOf("data1" to 12, "data2" to "value", "key" to "value"))
        }
        val cocoaBreadcrumb = CocoaBreadcrumb().apply {
            message = "test message"
            type = "test type"
            category = "test category"
            setData(mapOf("data1" to 12, "data2" to "value"))
        }
        val modifyBreadcrumbCallback: (Breadcrumb) -> Breadcrumb = {
            it.apply {
                setData("key", "value")
                message = "changed message"
            }
        }
        val kmpBreadcrumb = cocoaBreadcrumb?.toSentryBreadcrumb()
        val modifiedBreadcrumb = kmpBreadcrumb?.let { modifyBreadcrumbCallback.invoke(it).toCocoaBreadcrumb() }

        assertEquals(expectedCocoaBreadcrumb.toSentryBreadcrumb(), modifiedBreadcrumb?.toSentryBreadcrumb())
        assertNotEquals(cocoaBreadcrumb.toSentryBreadcrumb(), modifiedBreadcrumb?.toSentryBreadcrumb())
    }
}
