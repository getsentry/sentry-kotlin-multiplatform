package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import kotlin.js.JsName
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

    @JsName("Given_DebugBreadcrumb_Then_HasProperValues")
    @Test
    fun `Debug Breadcrumb has proper type and message and level`() {
        val breadcrumb = Breadcrumb.debug(testMessage)

        assertEquals(breadcrumb.level, SentryLevel.DEBUG)
        assertEquals(breadcrumb.message, testMessage)
        assertEquals(breadcrumb.type, "debug")
    }

    @JsName("Given_InfoBreadcrumb_Then_HasProperValues")
    @Test
    fun `Info Breadcrumb has proper type and message and level`() {
        val breadcrumb = Breadcrumb.info(testMessage)

        assertEquals(breadcrumb.level, SentryLevel.INFO)
        assertEquals(breadcrumb.message, testMessage)
        assertEquals(breadcrumb.type, "info")
    }

    @JsName("Given_ErrorBreadcrumb_Then_HasProperValues")
    @Test
    fun `Error Breadcrumb has proper type and message and level`() {
        val breadcrumb = Breadcrumb.error(testMessage)

        assertEquals(breadcrumb.type, "error")
        assertEquals(breadcrumb.level, SentryLevel.ERROR)
        assertEquals(breadcrumb.message, testMessage)
    }

    @JsName("Given_UserBreadcrumb_Then_HasProperValues")
    @Test
    fun `User Breadcrumb has proper type and message and category`() {
        val breadcrumb = Breadcrumb.user(testCategory, testMessage)

        assertEquals(breadcrumb.category, testCategory)
        assertEquals(breadcrumb.message, testMessage)
        assertEquals(breadcrumb.type, "user")
    }

    @JsName("Given_HTTPBreadcrumb_Then_HasProperValues")
    @Test
    fun `HTTP Breadcrumb has proper type and data and category`() {
        val breadcrumb = Breadcrumb.http(testUrl, testMethod)

        assertEquals(breadcrumb.getData()?.get("url"), testUrl)
        assertEquals(breadcrumb.getData()?.get("method"), testMethod)
        assertEquals(breadcrumb.type, "http")
    }

    @JsName("Given_HTTPBreadcrumb_Then_HasProperValuesAndStatusCode")
    @Test
    fun `HTTP Breadcrumb has proper type and data and category and status code`() {
        val breadcrumb = Breadcrumb.http(testUrl, testMethod, testStatusCode)

        assertEquals(breadcrumb.getData()?.get("url"), testUrl)
        assertEquals(breadcrumb.getData()?.get("method"), testMethod)
        assertEquals(breadcrumb.getData()?.get("status_code"), testStatusCode)
        assertEquals(breadcrumb.type, "http")
    }

    @JsName("Given_NavigationBreadcrumb_Then_HasProperValues")
    @Test
    fun `Navigation Breadcrumb has proper type and data and category`() {
        val breadcrumb = Breadcrumb.navigation(testFromNav, testToNav)

        assertEquals(breadcrumb.type, "navigation")
        assertEquals(breadcrumb.category, "navigation")
        assertEquals(breadcrumb.getData()?.get("from"), testFromNav)
        assertEquals(breadcrumb.getData()?.get("to"), testToNav)
    }

    @JsName("Given_TransactionBreadcrumb_Then_HasProperValues")
    @Test
    fun `Transaction Breadcrumb has proper type and message and category`() {
        val breadcrumb = Breadcrumb.transaction(testMessage)

        assertEquals(breadcrumb.type, "default")
        assertEquals(breadcrumb.category, "sentry.transaction")
        assertEquals(breadcrumb.message, testMessage)
    }

    @JsName("Given_QueryBreadcrumb_Then_HasProperValues")
    @Test
    fun `Query Breadcrumb has proper type and message`() {
        val breadcrumb = Breadcrumb.query(testMessage)

        assertEquals(breadcrumb.type, "query")
        assertEquals(breadcrumb.message, testMessage)
    }

    @JsName("Given_UIBreadcrumb_Then_HasProperValues")
    @Test
    fun `UI Breadcrumb has proper type and message and category`() {
        val breadcrumb = Breadcrumb.ui(testCategory, testMessage)

        assertEquals(breadcrumb.type, "default")
        assertEquals(breadcrumb.message, testMessage)
        assertEquals(breadcrumb.category, "ui.$testCategory")
    }

    @JsName("Given_UserInteractionBreadcrumb_Then_HasProperValues")
    @Test
    fun `User Interaction Breadcrumb has proper type and message and category and viewId and viewClass and level`() {
        val breadcrumb = Breadcrumb.userInteraction(testSubCategory, testViewId, testViewClass)

        assertEquals(breadcrumb.type, "user")
        assertEquals(breadcrumb.level, SentryLevel.INFO)
        assertEquals(breadcrumb.category, "ui.$testSubCategory")
        assertEquals(breadcrumb.getData()?.get("view.id"), testViewId)
        assertEquals(breadcrumb.getData()?.get("view.class"), testViewClass)
    }

    @JsName("Given_UserInteractionBreadcrumb_Then_HasProperValuesAndAdditionalDataAndLevel")
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

    @JsName("Given_SetDataWithMap_Then_DataIsSetProperly")
    @Test
    fun `setData with map as value sets values properly`() {
        val breadcrumb = Breadcrumb.debug(testMessage)
        val map = mutableMapOf<String, Any>("TestEntry" to "TestValue", "TestEntry2" to 12)
        breadcrumb.setData(map)

        assertEquals(map, breadcrumb.getData())
    }

    @JsName("Given_SetDataWithPrimitiveTypes_Then_DataIsSetProperly")
    @Test
    fun `setData with primitive types as value sets values properly`() {
        val breadcrumb = Breadcrumb.debug(testMessage)
        val expected: Map<String, Any>? = mapOf("keyExample" to "valueExample", "keyExample2" to 12, "keyExample3" to false)
        breadcrumb.setData("keyExample", "valueExample")
        breadcrumb.setData("keyExample2", 12)
        breadcrumb.setData("keyExample3", false)
        assertEquals(expected, breadcrumb.getData())
    }

    @JsName("Given_SetDataWithSet_Then_DataIsSetProperly")
    @Test
    fun `using setType sets values properly`() {
        val breadcrumb = Breadcrumb()
        breadcrumb.type = testCategory

        assertEquals(testCategory, breadcrumb.type)
    }

    @JsName("Given_SetMessage_Then_DataIsSetProperly")
    @Test
    fun `using setMessage sets values properly`() {
        val breadcrumb = Breadcrumb()
        breadcrumb.message = testMessage

        assertEquals(testMessage, breadcrumb.message)
    }

    @JsName("Given_SetLevel_Then_DataIsSetProperly")
    @Test
    fun `using setLevel sets values properly`() {
        val breadcrumb = Breadcrumb()
        breadcrumb.level = SentryLevel.DEBUG

        assertEquals(SentryLevel.DEBUG, breadcrumb.level)
    }

    @JsName("When_AccessingSetData_Then_DoesNotThrowNullPointerException")
    @Test
    fun `accessing setData with key value does not throw NullPointerException`() {
        val breadcrumb = Breadcrumb()

        breadcrumb.setData("key", "value")
    }
}
