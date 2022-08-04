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

        assertEquals(breadcrumb.getLevel(), SentryLevel.DEBUG)
        assertEquals(breadcrumb.getMessage(), testMessage)
        assertEquals(breadcrumb.getType(), "debug")
    }

    @Test
    fun `Info Breadcrumb has proper type and message and level`() {
        val breadcrumb = Breadcrumb.info(testMessage)

        assertEquals(breadcrumb.getLevel(), SentryLevel.INFO)
        assertEquals(breadcrumb.getMessage(), testMessage)
        assertEquals(breadcrumb.getType(), "info")
    }

    @Test
    fun `Error Breadcrumb has proper type and message and level`() {
        val breadcrumb = Breadcrumb.error(testMessage)

        assertEquals(breadcrumb.getType(), "error")
        assertEquals(breadcrumb.getLevel(), SentryLevel.ERROR)
        assertEquals(breadcrumb.getMessage(), testMessage)
    }

    @Test
    fun `User Breadcrumb has proper type and message and category`() {
        val breadcrumb = Breadcrumb.user(testCategory, testMessage)

        assertEquals(breadcrumb.getCategory(), testCategory)
        assertEquals(breadcrumb.getMessage(), testMessage)
        assertEquals(breadcrumb.getType(), "user")
    }

    @Test
    fun `HTTP Breadcrumb has proper type and data and category`() {
        val breadcrumb = Breadcrumb.http(testUrl, testMethod)

        assertEquals(breadcrumb.getData()["url"], testUrl)
        assertEquals(breadcrumb.getData()["method"], testMethod)
        assertEquals(breadcrumb.getType(), "http")
    }

    @Test
    fun `HTTP Breadcrumb has proper type and data and category and status code`() {
        val breadcrumb = Breadcrumb.http(testUrl, testMethod, testStatusCode)

        assertEquals(breadcrumb.getData()["url"], testUrl)
        assertEquals(breadcrumb.getData()["method"], testMethod)
        assertEquals(breadcrumb.getData()["status_code"], testStatusCode)
        assertEquals(breadcrumb.getType(), "http")
    }

    @Test
    fun `Navigation Breadcrumb has proper type and data and category`() {
        val breadcrumb = Breadcrumb.navigation(testFromNav, testToNav)

        assertEquals(breadcrumb.getType(), "navigation")
        assertEquals(breadcrumb.getCategory(), "navigation")
        assertEquals(breadcrumb.getData()["from"], testFromNav)
        assertEquals(breadcrumb.getData()["to"], testToNav)
    }

    @Test
    fun `Transaction Breadcrumb has proper type and message and category`() {
        val breadcrumb = Breadcrumb.transaction(testMessage)

        assertEquals(breadcrumb.getType(), "default")
        assertEquals(breadcrumb.getCategory(), "sentry.transaction")
        assertEquals(breadcrumb.getMessage(), testMessage)
    }

    @Test
    fun `Query Breadcrumb has proper type and message`() {
        val breadcrumb = Breadcrumb.query(testMessage)

        assertEquals(breadcrumb.getType(), "query")
        assertEquals(breadcrumb.getMessage(), testMessage)
    }

    @Test
    fun `UI Breadcrumb has proper type and message and category`() {
        val breadcrumb = Breadcrumb.ui(testCategory, testMessage)

        assertEquals(breadcrumb.getType(), "default")
        assertEquals(breadcrumb.getMessage(), testMessage)
        assertEquals(breadcrumb.getCategory(), "ui.$testCategory")
    }

    @Test
    fun `User Interaction Breadcrumb has proper type and message and category and viewId and viewClass and level`() {
        val breadcrumb = Breadcrumb.userInteraction(testSubCategory, testViewId, testViewClass)

        assertEquals(breadcrumb.getType(), "user")
        assertEquals(breadcrumb.getLevel(), SentryLevel.INFO)
        assertEquals(breadcrumb.getCategory(), "ui.$testSubCategory")
        assertEquals(breadcrumb.getData()["view.id"], testViewId)
        assertEquals(breadcrumb.getData()["view.class"], testViewClass)
    }

    @Test
    fun `User Interaction Breadcrumb has proper type and message and category and viewId and viewClass and additionalData and level`() {
        val additionalData = HashMap<String?, Any?>()
        additionalData.put("message", testMessage)
        additionalData.put("category", testCategory)

        val breadcrumb = Breadcrumb.userInteraction(testSubCategory, testViewId, testViewClass, additionalData)

        assertEquals(breadcrumb.getType(), "user")
        assertEquals(breadcrumb.getLevel(), SentryLevel.INFO)
        assertEquals(breadcrumb.getCategory(), "ui.$testSubCategory")
        assertEquals(breadcrumb.getData()["view.id"], testViewId)
        assertEquals(breadcrumb.getData()["view.class"], testViewClass)
        assertEquals(breadcrumb.getData()["message"], testMessage)
        assertEquals(breadcrumb.getData()["category"], testCategory)
    }

    @Test
    fun `setData with map as value sets values properly`() {
        val breadcrumb = Breadcrumb.debug(testMessage)
        val map = mutableMapOf("TestEntry" to "TestValue", "TestEntry2" to 12) as MutableMap<String, Any>
        breadcrumb.setData(map)

        assertEquals(map, breadcrumb.getData())
    }

    @Test
    fun `setData with primitive types as value sets values properly`() {
        val breadcrumb = Breadcrumb.debug(testMessage)
        val expected = mapOf("keyExample" to "valueExample", "keyExample2" to 12, "keyExample3" to false) as MutableMap<String, Any>
        breadcrumb.setData("keyExample", "valueExample")
        breadcrumb.setData("keyExample2", 12)
        breadcrumb.setData("keyExample3", false)
        assertEquals(expected, breadcrumb.getData())
    }

    @Test
    fun `using setType sets values properly`() {
        val breadcrumb = Breadcrumb()
        breadcrumb.setType(testCategory)

        assertEquals(testCategory, breadcrumb.getType())
    }

    @Test
    fun `using setMessage sets values properly`() {
        val breadcrumb = Breadcrumb()
        breadcrumb.setMessage(testMessage)

        assertEquals(testMessage, breadcrumb.getMessage())
    }

    @Test
    fun `using setLevel sets values properly`() {
        val breadcrumb = Breadcrumb()
        breadcrumb.setLevel(SentryLevel.DEBUG)

        assertEquals(SentryLevel.DEBUG, breadcrumb.getLevel())
    }

    @Test
    fun `accessing setData with key value does not throw NullPointerException`() {
        val breadcrumb = Breadcrumb()

        breadcrumb.setData("key", "value")
    }
}
