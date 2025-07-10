import io.sentry.kotlin.multiplatform.gradle.CocoaFrameworkLinker
import io.sentry.kotlin.multiplatform.gradle.FrameworkLinker
import io.sentry.kotlin.multiplatform.gradle.FrameworkPathResolver
import io.sentry.kotlin.multiplatform.gradle.FrameworkPaths
import io.sentry.kotlin.multiplatform.gradle.FrameworkResolutionStrategy
import io.sentry.kotlin.multiplatform.gradle.isAppleTarget
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CocoaFrameworkLinkerTest {
    private lateinit var fixture: Fixture

    @BeforeEach
    fun setUp() {
        fixture = Fixture()
    }

    @Test
    fun `framework linking succeeds for static Framework binary`() {
        val kmpExtension = fixture.project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        val appleTargets = listOf(
            kmpExtension.iosSimulatorArm64(),
            kmpExtension.iosArm64(),
            kmpExtension.watchosArm32(),
            kmpExtension.watchosSimulatorArm64(),
            kmpExtension.watchosX64(),
            kmpExtension.macosArm64(),
            kmpExtension.macosX64(),
            kmpExtension.tvosArm64(),
            kmpExtension.tvosSimulatorArm64(),
            kmpExtension.tvosX64()
        )
        appleTargets.forEach {
            it.binaries.framework {
                baseName = "MyFramework"
                isStatic = true
            }
        }

        val sut = fixture.getSut()
        sut.configure(appleTargets)

        appleTargets.forEach { target ->
            val binary = target.binaries.find { it.baseName == "MyFramework" }!!
            assertTrue(binary.linkerOpts.size == 1)
            assertEquals(binary.linkerOpts.first(), "-F$staticPath")
        }
    }

    @Test
    fun `framework linking succeeds for dynamic Framework binary`() {
        val kmpExtension = fixture.project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        val appleTargets = listOf(
            kmpExtension.iosSimulatorArm64(),
            kmpExtension.iosArm64(),
            kmpExtension.watchosArm32(),
            kmpExtension.watchosSimulatorArm64(),
            kmpExtension.watchosX64(),
            kmpExtension.macosArm64(),
            kmpExtension.macosX64(),
            kmpExtension.tvosArm64(),
            kmpExtension.tvosSimulatorArm64(),
            kmpExtension.tvosX64()
        )
        appleTargets.forEach {
            it.binaries.framework {
                baseName = "MyFramework"
                isStatic = false
            }
        }

        val sut = fixture.getSut()
        sut.configure(appleTargets)

        appleTargets.forEach { target ->
            val binary = target.binaries.find { it.baseName == "MyFramework" }!!
            assertTrue(binary.linkerOpts.size == 1)
            assertEquals(binary.linkerOpts.first(), "-F$dynamicPath")
        }
    }

    @Test
    fun `framework linking succeeds for TestExecutable binary`() {
        val kmpExtension = fixture.project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        val appleTargets = listOf(
            kmpExtension.iosSimulatorArm64(),
            kmpExtension.iosArm64(),
            kmpExtension.watchosArm32(),
            kmpExtension.watchosSimulatorArm64(),
            kmpExtension.watchosX64(),
            kmpExtension.macosArm64(),
            kmpExtension.macosX64(),
            kmpExtension.tvosArm64(),
            kmpExtension.tvosSimulatorArm64(),
            kmpExtension.tvosX64()
        )
        appleTargets.forEach {
            it.binaries.framework {
                baseName = "MyFramework"
                isStatic = true
            }
        }

        val sut = fixture.getSut()
        sut.configure(appleTargets)

        // both dynamic and static frameworks can be used for linkin in test executables
        appleTargets.forEach { target ->
            val binary = target.binaries.find { it is TestExecutable }!!
            assertTrue(binary.linkerOpts.size == 3)
            assertEquals(binary.linkerOpts.first(), "-rpath")
            assertTrue(binary.linkerOpts[1] == staticPath || binary.linkerOpts[1] == dynamicPath)
            assertTrue(binary.linkerOpts[2] == "-F$staticPath" || binary.linkerOpts[2] == "-F$dynamicPath")
        }
    }

    @Test
    fun `configure skips non-Apple targets and processes only Apple targets`() {
        val kmpExtension = fixture.project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        val appleTargets = listOf(
            kmpExtension.iosSimulatorArm64(),
            kmpExtension.iosArm64(),
            kmpExtension.macosArm64()
        )
        val nonAppleTargets = listOf(
            kmpExtension.linuxX64(),
            kmpExtension.mingwX64()
        )
        val allTargets = appleTargets + nonAppleTargets

        // Set up frameworks for Apple targets only
        appleTargets.forEach {
            it.binaries.framework {
                baseName = "MyFramework"
                isStatic = true
            }
        }

        val sut = fixture.getSut()
        sut.configure(allTargets)

        // Verify Apple targets were processed
        appleTargets.forEach { target ->
            val binary = target.binaries.find { it.baseName == "MyFramework" }!!
            assertTrue(binary.linkerOpts.size == 1)
            assertEquals(binary.linkerOpts.first(), "-F$staticPath")
        }

        // Verify non-Apple targets were skipped (they don't have framework binaries with linker options)
        nonAppleTargets.forEach { target ->
            // Non-Apple targets may have default binaries, but none should have Sentry framework linking
            target.binaries.forEach { binary ->
                // No binary should have our specific linker options 
                assertFalse(binary.linkerOpts.any { it.startsWith("-F") && it.contains("Sentry") })
            }
        }
    }

    @Test
    fun `configure skips all targets when no Apple targets are present`() {
        val kmpExtension = fixture.project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        val nonAppleTargets = listOf(
            kmpExtension.linuxX64(),
            kmpExtension.mingwX64()
        )

        val sut = fixture.getSut()
        sut.configure(nonAppleTargets)

        // Verify that no Sentry framework linking was applied to any binary
        nonAppleTargets.forEach { target ->
            target.binaries.forEach { binary ->
                // No binary should have our specific linker options 
                assertFalse(binary.linkerOpts.any { it.startsWith("-F") && it.contains("Sentry") })
            }
        }
    }

    @Test
    fun `isAppleTarget returns true for Apple targets`() {
        val kmpExtension = fixture.project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        val appleTargets = listOf(
            kmpExtension.iosSimulatorArm64(),
            kmpExtension.iosArm64(),
            kmpExtension.iosX64(),
            kmpExtension.macosArm64(),
            kmpExtension.macosX64(),
            kmpExtension.tvosArm64(),
            kmpExtension.tvosSimulatorArm64(),
            kmpExtension.tvosX64(),
            kmpExtension.watchosArm32(),
            kmpExtension.watchosArm64(),
            kmpExtension.watchosSimulatorArm64(),
            kmpExtension.watchosX64()
        )

        appleTargets.forEach { target ->
            assertTrue(target.isAppleTarget(), "Target ${target.name} should be detected as Apple target")
        }
    }

    @Test
    fun `isAppleTarget returns false for non-Apple targets`() {
        val kmpExtension = fixture.project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        val nonAppleTargets = listOf(
            kmpExtension.linuxX64(),
            kmpExtension.mingwX64()
        )

        nonAppleTargets.forEach { target ->
            assertFalse(target.isAppleTarget(), "Target ${target.name} should not be detected as Apple target")
        }
    }

    private class Fixture {
        val project: Project = ProjectBuilder.builder().build()

        init {
            project.pluginManager.apply {
                apply("org.jetbrains.kotlin.multiplatform")
                apply("io.sentry.kotlin.multiplatform.gradle")
            }
        }

        fun getSut(): CocoaFrameworkLinker {
            return CocoaFrameworkLinker(
                project.logger,
                FrameworkPathResolver(project, strategies = listOf(FakeStrategy())),
                FrameworkLinker(project.logger)
            )
        }
    }
}

// We don't really care what the strategy exactly does in this test
// The strategies themselves are tested independently
private class FakeStrategy : FrameworkResolutionStrategy {
    override fun resolvePaths(architectures: Set<String>): FrameworkPaths {
        return FrameworkPaths(static = staticPath, dynamic = dynamicPath)
    }
}

private const val staticPath = "/path/to/static/Sentry.xcframework"
private const val dynamicPath = "/path/to/dynamic/Sentry-Dynamic.xcframework"
