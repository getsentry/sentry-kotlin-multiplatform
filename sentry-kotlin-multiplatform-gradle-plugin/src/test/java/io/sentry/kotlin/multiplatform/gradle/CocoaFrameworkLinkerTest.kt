//import io.mockk.*
//import io.mockk.impl.annotations.MockK
//import io.mockk.junit5.MockKExtension
//import io.sentry.kotlin.multiplatform.gradle.*
//import org.gradle.api.logging.Logger
//import org.gradle.testfixtures.ProjectBuilder
//import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
//import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.extension.ExtendWith
//
//@ExtendWith(MockKExtension::class)
//class CocoaFrameworkLinkerTest {
//    @MockK
//    lateinit var mockLogger: Logger
//
//    @MockK
//    lateinit var mockPathResolver: FrameworkPathResolver
//
//    @MockK
//    lateinit var mockFrameworkLinker: FrameworkLinker
//
//    @MockK
//    lateinit var mockLinkerExtension: LinkerExtension
//
//    private lateinit var linker: CocoaFrameworkLinker
//
//    @BeforeEach
//    fun setup() {
//        every { mockLogger.info(any()) } just Runs
//        linker = CocoaFrameworkLinker(mockLogger, mockPathResolver, mockFrameworkLinker)
//    }
//
//    @Test
//    fun `configure should skip when not on macOS host`() {
//        linker.configure(mockLinkerExtension, emptyList(), hostIsMac = false)
//
//        verify { mockLogger.info("Skipping Apple framework linking: Requires macOS host") }
//        verify { mockPathResolver wasNot Called }
//        verify { mockFrameworkLinker wasNot Called }
//    }
//
//    @Test
//    fun `integration test`() {
//        val project = ProjectBuilder.builder().build()
//
//        project.pluginManager.apply {
//            apply("org.jetbrains.kotlin.multiplatform")
//            apply("io.sentry.kotlin.multiplatform.gradle")
//        }
//
//        val kmpExtension = project.extensions.getByName("kotlin") as KotlinMultiplatformExtension
//        kmpExtension.apply {
//            listOf(
//                iosArm64(),
//            ).forEach {
//                it.binaries.framework {
//                    baseName = "shared"
//                    isStatic = false
//                }
//            }
//        }
//
//        val linker2 = project.extensions.getByName("linker") as LinkerExtension
//        CocoaFrameworkLinker(
//            project.logger,
//            FrameworkPathResolver2(),
//            FrameworkLinker(project.logger)
//        ).configure(linker2, kmpExtension.appleTargets().toList(), hostIsMac = true)
//
//        kmpExtension.iosArm64().binaries.filterIsInstance<Framework>().forEach {
//            println(it.linkerOpts)
//        }
//    }
//
//    @Test
//    fun `configure should process valid Apple targets`() {
//        val project = ProjectBuilder.builder().build()
//
//        project.pluginManager.apply {
//            apply("org.jetbrains.kotlin.multiplatform")
//            apply("io.sentry.kotlin.multiplatform.gradle")
//        }
//
//        val kmpExtension = project.extensions.getByName("kotlin") as KotlinMultiplatformExtension
//        kmpExtension.apply {
//            listOf(
//                iosArm64(),
//            ).forEach {
//                it.binaries.framework {
//                    baseName = "shared"
//                    isStatic = false
//                }
//            }
//        }
//
//        every { mockPathResolver.resolvePaths(any(), any()) } returns ("dynamic/path" to "static/path")
//        justRun { mockFrameworkLinker.configureBinaries(any(), any(), any()) }
//
//        linker.configure(mockLinkerExtension, kmpExtension.appleTargets().toList(), hostIsMac = true)
//
//        verifyAll {
//            mockPathResolver.resolvePaths(mockLinkerExtension, setOf("ios-arm64", "ios-arm64_arm64e"))
//            mockFrameworkLinker.configureBinaries(any(), "dynamic/path", "static/path")
//        }
//    }
//}
//
//private class Fixture {
//    fun getSut(): CocoaFrameworkLinker {
//        return CocoaFrameworkLinker()
//    }
//}