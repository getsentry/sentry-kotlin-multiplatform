package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.utils.fakeDsn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class BeforeBreadcrumbIntegrationTest {
    private val breadcrumbConfigurator = BreadcrumbConfigurator()

    @Test
    fun `breadcrumb is not null if KMP beforeBreadcrumb callback config is null`() {
        val breadcrumb = breadcrumbConfigurator.applyOptions {
            it.dsn = fakeDsn
        }
        assertNotNull(breadcrumb)
    }

    @Test
    fun `breadcrumb is null if KMP beforeBreadcrumb callback config returns null`() {
        val breadcrumb = breadcrumbConfigurator.applyOptions {
            it.dsn = fakeDsn
            it.beforeBreadcrumb = {
                null
            }
        }
        assertNull(breadcrumb)
    }

    @Test
    fun `breadcrumb is not null if KMP beforeBreadcrumb callback config returns not null`() {
        val breadcrumb = breadcrumbConfigurator.applyOptions {
            it.dsn = fakeDsn
            it.beforeBreadcrumb = { breadcrumb ->
                breadcrumb
            }
        }
        assertNotNull(breadcrumb)
    }

    @Test
    fun `breadcrumb level is not modified if KMP beforeBreadcrumb callback config does not modify it`() {
        val originalBreadcrumb = breadcrumbConfigurator.originalBreadcrumb
        val modifiedBreadcrumb = breadcrumbConfigurator.applyOptions {
            it.dsn = fakeDsn
            it.beforeBreadcrumb = { breadcrumb ->
                breadcrumb
            }
        }
        assertEquals(originalBreadcrumb.level, modifiedBreadcrumb?.level)
    }

    @Test
    fun `breadcrumb level is modified if KMP beforeBreadcrumb callback config modifies it`() {
        val modifiedBreadcrumb = breadcrumbConfigurator.applyOptions {
            it.dsn = fakeDsn
            it.beforeBreadcrumb = { breadcrumb ->
                breadcrumb.level = SentryLevel.WARNING
                breadcrumb
            }
        }
        assertEquals(SentryLevel.WARNING, modifiedBreadcrumb?.level)
    }

    @Test
    fun `breadcrumb category is not modified if KMP beforeBreadcrumb callback config does not modify it`() {
        val originalBreadcrumb = breadcrumbConfigurator.originalBreadcrumb
        val modifiedBreadcrumb = breadcrumbConfigurator.applyOptions {
            it.dsn = fakeDsn
            it.beforeBreadcrumb = { breadcrumb ->
                breadcrumb
            }
        }
        assertEquals(originalBreadcrumb.category, modifiedBreadcrumb?.category)
    }

    @Test
    fun `breadcrumb category is modified if KMP beforeBreadcrumb callback config modifies it`() {
        val modifiedBreadcrumb = breadcrumbConfigurator.applyOptions {
            it.dsn = fakeDsn
            it.beforeBreadcrumb = { breadcrumb ->
                breadcrumb.category = "category"
                breadcrumb
            }
        }
        assertEquals("category", modifiedBreadcrumb?.category)
    }

    @Test
    fun `breadcrumb type is not modified if KMP beforeBreadcrumb callback config does not modify it`() {
        val originalBreadcrumb = breadcrumbConfigurator.originalBreadcrumb
        val modifiedBreadcrumb = breadcrumbConfigurator.applyOptions {
            it.dsn = fakeDsn
            it.beforeBreadcrumb = { breadcrumb ->
                breadcrumb
            }
        }
        assertEquals(originalBreadcrumb.type, modifiedBreadcrumb?.type)
    }

    @Test
    fun `breadcrumb type is modified if KMP beforeBreadcrumb callback config modifies it`() {
        val modifiedBreadcrumb = breadcrumbConfigurator.applyOptions {
            it.dsn = fakeDsn
            it.beforeBreadcrumb = { breadcrumb ->
                breadcrumb.type = "type"
                breadcrumb
            }
        }
        assertEquals("type", modifiedBreadcrumb?.type)
    }

    @Test
    fun `breadcrumb message is not modified if KMP beforeBreadcrumb callback config does not modify it`() {
        val originalBreadcrumb = breadcrumbConfigurator.originalBreadcrumb
        val modifiedBreadcrumb = breadcrumbConfigurator.applyOptions {
            it.dsn = fakeDsn
            it.beforeBreadcrumb = { breadcrumb ->
                breadcrumb
            }
        }
        assertEquals(originalBreadcrumb.level, modifiedBreadcrumb?.level)
    }

    @Test
    fun `breadcrumb message is modified if KMP beforeBreadcrumb callback config modifies it`() {
        val modifiedBreadcrumb = breadcrumbConfigurator.applyOptions {
            it.dsn = fakeDsn
            it.beforeBreadcrumb = { breadcrumb ->
                breadcrumb.message = "message"
                breadcrumb
            }
        }
        assertEquals("message", modifiedBreadcrumb?.message)
    }
}
