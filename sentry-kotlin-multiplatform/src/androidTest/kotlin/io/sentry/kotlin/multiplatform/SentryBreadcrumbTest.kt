package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toAndroidBreadcrumb
import io.sentry.kotlin.multiplatform.extensions.toKMPSentryLevel
import io.sentry.kotlin.multiplatform.protocol.SentryBreadcrumb
import org.junit.Test
import kotlin.test.assertEquals

class AndroidSentryBreadcrumbTest {

    @Test
    fun `convert SentryBreadcrumb to AndroidBreadcrumb has proper values`() {
        val sentryBreadcrumb = SentryBreadcrumb.debug("test")
        sentryBreadcrumb.setData("my test", "value")
        val androidBreadcrumb = sentryBreadcrumb.toAndroidBreadcrumb()

        assertEquals(sentryBreadcrumb.getData(), androidBreadcrumb.data)
        assertEquals(sentryBreadcrumb.getLevel(), androidBreadcrumb.level?.toKMPSentryLevel())
        assertEquals(sentryBreadcrumb.getType(), androidBreadcrumb.type)
        assertEquals(sentryBreadcrumb.getMessage(), androidBreadcrumb.message)
        assertEquals(sentryBreadcrumb.getCategory(), androidBreadcrumb.category)
    }
}
