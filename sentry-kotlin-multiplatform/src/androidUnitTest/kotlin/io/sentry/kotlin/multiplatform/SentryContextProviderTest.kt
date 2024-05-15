package io.sentry.kotlin.multiplatform

import android.content.ContentValues
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import kotlin.test.assertNotNull

@RunWith(AndroidJUnit4::class)
class SentryContextProviderTest : BaseSentryTest() {
    private lateinit var provider: SentryContextProvider

    @Before
    override fun setUp() {
        provider = SentryContextProvider()
    }

    // We create a nested class so this test is executed with the BeforeEach method that initializes
    // the actual content provider and not just a mock.
    class SentryContextOnCreateTest : BaseSentryTest() {
        @Test
        fun `onCreate initializes applicationContext`() {
            // Simple call to the applicationContext to make sure it's initialized
            assertNotNull(applicationContext)
        }
    }

    fun `create does not throw Exception`() {
        provider.onCreate()
    }

    @Test(expected = IllegalStateException::class)
    fun `insert throws Exception`() {
        val uri = mock(Uri::class.java)
        val values = ContentValues()
        provider.insert(uri, values)
    }

    @Test(expected = IllegalStateException::class)
    fun `update throws Exception`() {
        val uri = mock(Uri::class.java)
        val values = ContentValues()
        provider.update(uri, values, null, null)
    }

    @Test(expected = IllegalStateException::class)
    fun `delete throws Exception`() {
        val uri = mock(Uri::class.java)
        provider.delete(uri, null, null)
    }

    @Test(expected = IllegalStateException::class)
    fun `getType throws Exception`() {
        val uri = mock(Uri::class.java)
        provider.getType(uri)
    }
}
