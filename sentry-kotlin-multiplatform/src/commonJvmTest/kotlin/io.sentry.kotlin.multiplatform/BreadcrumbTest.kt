package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toJvmBreadcrumb
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import org.junit.Test
import kotlin.test.assertEquals

class JvmBreadcrumbTest {

    @Test
    fun `convert SentryBreadcrumb to JvmBreadcrumb has proper values`() {
        val sentryBreadcrumb = Breadcrumb.debug("test")
        sentryBreadcrumb.setData("my test", "value")
        sentryBreadcrumb.setMessage("test message")
        sentryBreadcrumb.setType("user")
        sentryBreadcrumb.setLevel(SentryLevel.ERROR)
        val androidBreadcrumb = sentryBreadcrumb.toJvmBreadcrumb()

        assertEquals(sentryBreadcrumb.getData(), androidBreadcrumb.data)
        assertEquals(sentryBreadcrumb.getLevel(), androidBreadcrumb.level?.toKmpSentryLevel())
        assertEquals(sentryBreadcrumb.getType(), androidBreadcrumb.type)
        assertEquals(sentryBreadcrumb.getMessage(), androidBreadcrumb.message)
        assertEquals(sentryBreadcrumb.getCategory(), androidBreadcrumb.category)
    }
}
