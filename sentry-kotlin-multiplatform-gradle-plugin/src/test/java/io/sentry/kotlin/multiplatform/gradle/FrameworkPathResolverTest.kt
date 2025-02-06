package io.sentry.kotlin.multiplatform.gradle

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.gradle.internal.impldep.org.junit.Assert.assertEquals
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FrameworkPathResolverTest {
    private val mockStrategy1 = mockk<FrameworkResolutionStrategy>()
    private val mockStrategy2 = mockk<FrameworkResolutionStrategy>()
    private val mockStrategy3 = mockk<FrameworkResolutionStrategy>()
    private lateinit var fixture: Fixture

    @BeforeEach
    fun setUp() {
        fixture = Fixture()
    }

    @Test
    fun `does not execute subsequent strategies after first success`() {
        val strategy1 = mockk<FrameworkResolutionStrategy> {
            every { resolvePaths(any()) } returns FrameworkPaths(dynamic = "dyn")
        }
        val strategy2 = mockk<FrameworkResolutionStrategy>()

        val sut = fixture.getSut(listOf(strategy1, strategy2))
        sut.resolvePaths("test")

        verify(exactly = 0) { strategy2.resolvePaths(any()) }
    }

    @Test
    fun `proceeds past multiple failing strategies and returns first success`() {
        every { mockStrategy1.resolvePaths(any()) } throws FrameworkLinkingException("")
        every { mockStrategy2.resolvePaths(any()) } returns FrameworkPaths.NONE
        every { mockStrategy3.resolvePaths(any()) } returns FrameworkPaths(static = "valid")

        val sut = fixture.getSut(listOf(mockStrategy1, mockStrategy2, mockStrategy3))
        val result = sut.resolvePaths("test")

        assertEquals("valid", result.static)
    }

    @Test
    fun `throws if no framework paths are resolved by strategies`() {
        every { mockStrategy1.resolvePaths(any()) } returns FrameworkPaths.NONE
        every { mockStrategy2.resolvePaths(any()) } returns FrameworkPaths.NONE

        val sut = fixture.getSut(listOf(mockStrategy1, mockStrategy2))
        assertThrows<FrameworkLinkingException> {
            sut.resolvePaths("test")
        }

        verifyOrder {
            mockStrategy1.resolvePaths(any())
            mockStrategy2.resolvePaths(any())
        }
    }

    @Test
    fun `throws when no strategies provided`() {
        val sut = fixture.getSut(emptyList())

        assertThrows<FrameworkLinkingException> {
            sut.resolvePaths("test")
        }
    }

    private class Fixture {
        fun getSut(strategies: List<FrameworkResolutionStrategy>): FrameworkPathResolver {
            val project = ProjectBuilder.builder().build()

            project.pluginManager.apply {
                apply("org.jetbrains.kotlin.multiplatform")
                apply("io.sentry.kotlin.multiplatform.gradle")
            }

            return FrameworkPathResolver(project, strategies)
        }
    }
}
