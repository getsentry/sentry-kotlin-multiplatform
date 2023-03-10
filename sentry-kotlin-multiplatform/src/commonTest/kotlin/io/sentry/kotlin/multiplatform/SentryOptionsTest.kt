package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import kotlin.test.Test
import kotlin.test.assertEquals

class SentryOptionsTest {

    @Test
    fun `Breadcrumb can be modified via callback in init and should return the modified Breadcrumb`() {
        val options = SentryOptions()

        fun mockInit(configuration: (SentryOptions) -> Unit) {
            configuration.invoke(options)
        }

        val expectedBreadcrumb = Breadcrumb().apply {
            message = "changed message"
            type = "changed type"
            category = "changed category"
            setData(mutableMapOf("data1" to 12, "data2" to "value", "key" to "value"))
        }

        val breadcrumb = Breadcrumb().apply {
            message = "another message"
            type = "another type"
            category = "another category"
            setData(mutableMapOf("data1" to 12, "data2" to "value"))
        }

        mockInit {
            it.beforeBreadcrumb = { breadcrumb ->
                breadcrumb.apply {
                    setData("key", "value")
                    message = "changed message"
                    type = "changed type"
                    category = "changed category"
                }
            }
        }

        val modifiedBreadcrumb = breadcrumb.let(options.beforeBreadcrumb!!)

        assertEquals(expectedBreadcrumb, modifiedBreadcrumb)
    }

    @Test
    fun `Breadcrumb can be dropped via beforeBreadcrumb hook`() {
        val options = SentryOptions()

        fun mockInit(configuration: (SentryOptions) -> Unit) {
            configuration.invoke(options)
        }

        mockInit {
            it.beforeBreadcrumb = { breadcrumb ->
                breadcrumb.message = "message"
                null
            }
        }

        val modifiedBreadcrumb = options.beforeBreadcrumb?.invoke(Breadcrumb())

        assertEquals(null, modifiedBreadcrumb)
    }

}
