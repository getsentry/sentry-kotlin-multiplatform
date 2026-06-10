import io.sentry.kotlin.multiplatform.gradle.CocoaFrameworkLinker
import io.sentry.kotlin.multiplatform.gradle.FrameworkLinker
import io.sentry.kotlin.multiplatform.gradle.FrameworkPathResolver
import io.sentry.kotlin.multiplatform.gradle.FrameworkPaths
import io.sentry.kotlin.multiplatform.gradle.FrameworkResolutionStrategy
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable
import org.junit.jupiter.api.Assertions.assertEquals
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
