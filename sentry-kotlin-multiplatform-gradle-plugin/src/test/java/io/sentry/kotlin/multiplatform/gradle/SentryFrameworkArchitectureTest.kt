package io.sentry.kotlin.multiplatform.gradle

import io.mockk.every
import io.mockk.mockk
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class SentryFrameworkArchitectureTest {
    companion object {
        @JvmStatic
        fun architectureData(): List<Arguments> = listOf(
            Arguments.of("iosSimulatorArm64", "ios-arm64_x86_64-simulator"),
            Arguments.of("iosX64", "ios-arm64_x86_64-simulator"),
            Arguments.of("iosArm64", "ios-arm64"),
            Arguments.of("macosArm64", "macos-arm64_x86_64"),
            Arguments.of("macosX64", "macos-arm64_x86_64"),
            Arguments.of("tvosSimulatorArm64", "tvos-arm64_x86_64-simulator"),
            Arguments.of("tvosX64", "tvos-arm64_x86_64-simulator"),
            Arguments.of("tvosArm64", "tvos-arm64"),
            Arguments.of("watchosArm32", "watchos-arm64_arm64_32_armv7k"),
            Arguments.of("watchosArm64", "watchos-arm64_arm64_32_armv7k"),
            Arguments.of("watchosSimulatorArm64", "watchos-arm64_i386_x86_64-simulator"),
            Arguments.of("watchosX64", "watchos-arm64_i386_x86_64-simulator"),
            Arguments.of("unsupportedTarget", null)
        )
    }

    @ParameterizedTest(name = "Target {0} should return {1}")
    @MethodSource("architectureData")
    fun `toSentryFrameworkArchitecture returns correct architecture for all targets`(
        targetName: String,
        expectedArchitecture: String?
    ) {
        val target = mockk<KotlinNativeTarget>()
        every { target.name } returns targetName

        val result = target.toSentryFrameworkArchitecture()

        assertEquals(expectedArchitecture, result)
    }
}
