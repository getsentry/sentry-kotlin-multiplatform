package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import kotlin.test.Test
import kotlin.test.assertEquals

class BeforeBreadcrumbTest {
    @Test
    fun `beforeBreadcrumb drops breadcrumb`() {
        val options = SentryOptions()
        options.beforeBreadcrumb = {
            null
        }

        val breadcrumb = options.beforeBreadcrumb?.invoke(Breadcrumb())

        assertEquals(null, breadcrumb)
    }
}
