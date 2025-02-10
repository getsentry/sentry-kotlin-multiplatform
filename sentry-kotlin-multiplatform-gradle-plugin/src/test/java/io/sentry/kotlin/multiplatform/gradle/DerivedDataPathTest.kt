package io.sentry.kotlin.multiplatform.gradle

import io.mockk.every
import io.mockk.mockk
import org.gradle.api.Action
import org.gradle.process.ExecOperations
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream

class DerivedDataPathTest {
    private lateinit var valueSource: DerivedDataPathValueSource
    private lateinit var execOperations: ExecOperations
    private lateinit var parameters: DerivedDataPathValueSource.Parameters

    @BeforeEach
    fun setup() {
        execOperations = mockk()
        parameters = mockk()

        valueSource = object : DerivedDataPathValueSource() {
            override val execOperations: ExecOperations = this@DerivedDataPathTest.execOperations
            override fun getParameters(): Parameters {
                return this@DerivedDataPathTest.parameters
            }
        }
    }

    @Test
    fun `obtain should return correct derived data path`() {
        val xcodebuildOutput = """
            Build settings for action build and target MyTarget:
                BUILD_DIR = /DerivedData/Example/Build/Products
        """.trimIndent()

        every { parameters.xcodeprojPath } returns mockk {
            every { get() } returns "/path/to/project.xcodeproj"
        }

        every { execOperations.exec(any()) } answers {
            val execSpecLambda = it.invocation.args[0] as Action<ExecSpec>
            val mockExecSpec = mockk<ExecSpec>(relaxed = true)

            val buildDirOutput = ByteArrayOutputStream()
            buildDirOutput.write(xcodebuildOutput.toByteArray())

            var capturedOutputStream: ByteArrayOutputStream? = null
            every { mockExecSpec.commandLine }.returns(listOf())
            every { mockExecSpec.setStandardOutput(any()) } answers {
                capturedOutputStream = firstArg()
                mockExecSpec
            }

            execSpecLambda.execute(mockExecSpec)

            capturedOutputStream?.write(xcodebuildOutput.toByteArray())

            mockk<ExecResult> {
                every { exitValue } returns 0
            }
        }

        val result = valueSource.obtain()

        assertEquals("/DerivedData/Example", result)
    }

    @Test
    fun `obtain should return null when BUILD_DIR is not found`() {
        val xcodebuildOutput = "Some output without BUILD_DIR"

        every { parameters.xcodeprojPath } returns mockk {
            every { get() } returns "/path/to/project.xcodeproj"
        }

        every { execOperations.exec(any()) } answers {
            val execSpecLambda = it.invocation.args[0] as Action<ExecSpec>
            val mockExecSpec = mockk<ExecSpec>(relaxed = true)

            val buildDirOutput = ByteArrayOutputStream()
            buildDirOutput.write(xcodebuildOutput.toByteArray())

            var capturedOutputStream: ByteArrayOutputStream? = null
            every { mockExecSpec.commandLine }.returns(listOf())
            every { mockExecSpec.setStandardOutput(any()) } answers {
                capturedOutputStream = firstArg()
                mockExecSpec
            }

            execSpecLambda.execute(mockExecSpec)

            capturedOutputStream?.write(xcodebuildOutput.toByteArray())

            mockk<ExecResult> {
                every { exitValue } returns 0
            }
        }

        assertNull(valueSource.obtain())
    }
}
