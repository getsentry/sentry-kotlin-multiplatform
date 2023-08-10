package io.sentry.kotlin.multiplatform

import android.content.ContentValues
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class ContextProviderTest : BaseSentryTest() {
    private lateinit var provider: ContextProvider

    @Before
    override fun setUp() {
        provider = ContextProvider()
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
