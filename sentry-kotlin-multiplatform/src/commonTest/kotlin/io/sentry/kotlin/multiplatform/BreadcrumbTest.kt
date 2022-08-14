package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import kotlin.test.Test
import kotlin.test.assertEquals

class BreadcrumbTest {

    private val testMessage = "TestMessage"
    private val testCategory = "TestCategory"
    private val testUrl = "sentry.io"
    private val testMethod = "PUT"
    private val testStatusCode = 404
    private val testFromNav = "MainView"
    private val testToNav = "OrderView"
    private val testSubCategory = "SubCategory"
    private val testViewId = "MyCustomViewId"
    private val testViewClass = "MyCustomViewClass"

    @Test
    fun `Debug Breadcrumb has proper type and message and level`() {
        val breadcrumb = Breadcrumb.debug(testMessage)

        assertEquals(breadcrumb.level, SentryLevel.DEBUG)
        assertEquals(breadcrumb.message, testMessage)
        assertEquals(breadcrumb.type, "debug")
    }

    @Test
    fun `Info Breadcrumb has proper type and message and level`() {
        val breadcrumb = Breadcrumb.info(testMessage)

        assertEquals(breadcrumb.level, SentryLevel.INFO)
        assertEquals(breadcrumb.message, testMessage)
        assertEquals(breadcrumb.type, "info")
    }

    @Test
    fun `Error Breadcrumb has proper type and message and level`() {
        val breadcrumb = Breadcrumb.error(testMessage)

        assertEquals(breadcrumb.type, "error")
        assertEquals(breadcrumb.level, SentryLevel.ERROR)
        assertEquals(breadcrumb.message, testMessage)
    }

    @Test
    fun `User Breadcrumb has proper type and message and category`() {
        val breadcrumb = Breadcrumb.user(testCategory, testMessage)

        assertEquals(breadcrumb.category, testCategory)
        assertEquals(breadcrumb.message, testMessage)
        assertEquals(breadcrumb.type, "user")
    }

    @Test
    fun `HTTP Breadcrumb has proper type and data and category`() {
        val breadcrumb = Breadcrumb.http(testUrl, testMethod)

        assertEquals(breadcrumb.getData()?.get("url"), testUrl)
        assertEquals(breadcrumb.getData()?.get("method"), testMethod)
        assertEquals(breadcrumb.type, "http")
    }

    @Test
    fun `HTTP Breadcrumb has proper type and data and category and status code`() {
        val breadcrumb = Breadcrumb.http(testUrl, testMethod, testStatusCode)

        assertEquals(breadcrumb.getData()?.get("url"), testUrl)
        assertEquals(breadcrumb.getData()?.get("method"), testMethod)
        assertEquals(breadcrumb.getData()?.get("status_code"), testStatusCode)
        assertEquals(breadcrumb.type, "http")
    }

    @Test
    fun `Navigation Breadcrumb has proper type and data and category`() {
        val breadcrumb = Breadcrumb.navigation(testFromNav, testToNav)

        assertEquals(breadcrumb.type, "navigation")
        assertEquals(breadcrumb.category, "navigation")
        assertEquals(breadcrumb.getData()?.get("from"), testFromNav)
        assertEquals(breadcrumb.getData()?.get("to"), testToNav)
    }

    @Test
    fun `Transaction Breadcrumb has proper type and message and category`() {
        val breadcrumb = Breadcrumb.transaction(testMessage)

        assertEquals(breadcrumb.type, "default")
        assertEquals(breadcrumb.category, "sentry.transaction")
        assertEquals(breadcrumb.message, testMessage)
    }

    @Test
    fun `Query Breadcrumb has proper type and message`() {
        val breadcrumb = Breadcrumb.query(testMessage)

        assertEquals(breadcrumb.type, "query")
        assertEquals(breadcrumb.message, testMessage)
    }

    @Test
    fun `UI Breadcrumb has proper type and message and category`() {
        val breadcrumb = Breadcrumb.ui(testCategory, testMessage)

        assertEquals(breadcrumb.type, "default")
        assertEquals(breadcrumb.message, testMessage)
        assertEquals(breadcrumb.category, "ui.$testCategory")
    }

    @Test
    fun `User Interaction Breadcrumb has proper type and message and category and viewId and viewClass and level`() {
        val breadcrumb = Breadcrumb.userInteraction(testSubCategory, testViewId, testViewClass)

        assertEquals(breadcrumb.type, "user")
        assertEquals(breadcrumb.level, SentryLevel.INFO)
        assertEquals(breadcrumb.category, "ui.$testSubCategory")
        assertEquals(breadcrumb.getData()?.get("view.id"), testViewId)
        assertEquals(breadcrumb.getData()?.get("view.class"), testViewClass)
    }

    @Test
    fun `User Interaction Breadcrumb has proper type and message and category and viewId and viewClass and additionalData and level`() {
        val additionalData = HashMap<String?, Any?>()
        additionalData.put("message", testMessage)
        additionalData.put("category", testCategory)

        val breadcrumb = Breadcrumb.userInteraction(testSubCategory, testViewId, testViewClass, additionalData)

        assertEquals(breadcrumb.type, "user")
        assertEquals(breadcrumb.level, SentryLevel.INFO)
        assertEquals(breadcrumb.category, "ui.$testSubCategory")
        assertEquals(breadcrumb.getData()?.get("view.id"), testViewId)
        assertEquals(breadcrumb.getData()?.get("view.class"), testViewClass)
        assertEquals(breadcrumb.getData()?.get("message"), testMessage)
        assertEquals(breadcrumb.getData()?.get("category"), testCategory)
    }

    @Test
    fun `setData with map as value sets values properly`() {
        val breadcrumb = Breadcrumb.debug(testMessage)
        val map = mutableMapOf<String, Any>("TestEntry" to "TestValue", "TestEntry2" to 12)
        breadcrumb.setData(map)

        assertEquals(map, breadcrumb.getData())
    }

    @Test
    fun `setData with primitive types as value sets values properly`() {
        val breadcrumb = Breadcrumb.debug(testMessage)
        val expected: Map<String, Any>? = mapOf("keyExample" to "valueExample", "keyExample2" to 12, "keyExample3" to false)
        breadcrumb.setData("keyExample", "valueExample")
        breadcrumb.setData("keyExample2", 12)
        breadcrumb.setData("keyExample3", false)
        assertEquals(expected, breadcrumb.getData())
    }

    @Test
    fun `using setType sets values properly`() {
        val breadcrumb = Breadcrumb()
        breadcrumb.type = testCategory

        assertEquals(testCategory, breadcrumb.type)
    }

    @Test
    fun `using setMessage sets values properly`() {
        val breadcrumb = Breadcrumb()
        breadcrumb.message = testMessage

        assertEquals(testMessage, breadcrumb.message)
    }

    @Test
    fun `using setLevel sets values properly`() {
        val breadcrumb = Breadcrumb()
        breadcrumb.level = SentryLevel.DEBUG

        assertEquals(SentryLevel.DEBUG, breadcrumb.level)
    }

    @Test
    fun `accessing setData with key value does not throw NullPointerException`() {
        val breadcrumb = Breadcrumb()

        breadcrumb.setData("key", "value")
    }

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
}
