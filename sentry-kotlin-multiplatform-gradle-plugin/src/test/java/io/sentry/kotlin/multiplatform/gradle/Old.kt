package io.sentry.kotlin.multiplatform.gradle

//
//import io.mockk.Runs
//import io.mockk.every
//import io.mockk.impl.annotations.MockK
//import io.mockk.junit5.MockKExtension
//import io.mockk.just
//import io.mockk.verify
//import org.gradle.api.Project
//import org.gradle.api.logging.Logger
//import org.gradle.testfixtures.ProjectBuilder
//import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
//import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
//import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
//import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.extension.ExtendWith
//import java.io.File
//
//@ExtendWith(MockKExtension::class)
//class CocoaFrameworkLinkerTest {
//    @MockK
//    private lateinit var mockProject: Project
//
//    @MockK
//    private lateinit var mockLogger: Logger
//
//    @MockK
//    private lateinit var mockKmpExtension: KotlinMultiplatformExtension
//
//    @MockK
//    private lateinit var mockTarget: KotlinNativeTarget
//
//    @BeforeEach
//    fun setup() {
//        every { mockLogger.info(any()) } just Runs
//    }
//
//    @Test
//    fun `configure should skip when Kotlin Multiplatform extension is missing`() {
//        val project = ProjectBuilder.builder().build()
//        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")
//
//        val linker = CocoaFrameworkLinker(project, mockLogger, hostIsMac = true)
//        val linkerExtension = project.extensions.getByName("linker") as LinkerExtension
//        linker.configure(linkerExtension)
//
//        verify {
//            mockLogger.info("Skipping Apple framework linking: Kotlin Multiplatform extension not found")
//        }
//    }
//
//    @Test
//    fun `configure should skip when not on macOS host`() {
//        val project = ProjectBuilder.builder().build()
//        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
//        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")
//
//        val linker = CocoaFrameworkLinker(project, mockLogger, hostIsMac = false)
//        val linkerExtension = project.extensions.getByName("linker") as LinkerExtension
//        linker.configure(linkerExtension)
//
//        verify {
//            mockLogger.info("Skipping Apple framework linking: Requires macOS host")
//        }
//    }
//
//    @Test
//    fun `configure should process valid Apple targets with custom framework path`() {
//        val project = ProjectBuilder.builder().build()
//        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
//        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")
//
//        val kmpExtension = project.extensions.findByName("kotlin") as KotlinMultiplatformExtension
//        kmpExtension.apply {
//            iosArm64 {
//                binaries {
//                    framework("testFramework") {
//                        isStatic = true
//                    }
//                    test("testExecutable")
//                }
//            }
//        }
//
//        val linker = CocoaFrameworkLinker(project, mockLogger, hostIsMac = true)
//        val linkerExtension = project.extensions.getByName("linker") as LinkerExtension
//
//        val frameworkDir = File(File(System.getProperty("java.io.tmpdir")), "Sentry.xcframework").apply {
//            mkdirs()
//            deleteOnExit()
//            File(this, "ios-arm64").mkdirs()
//        }
//        linkerExtension.frameworkPath.set(frameworkDir.absolutePath)
//
//        linker.configure(linkerExtension)
//
//        val target = kmpExtension.targets.getByName("iosArm64") as KotlinNativeTarget
//
//        target.binaries.forEach { binary ->
//            when (binary) {
//                is Framework -> assert(binary.linkerOpts.any { it == "-F${frameworkDir.absolutePath}" })
//                is TestExecutable -> {
//                    assert(binary.linkerOpts.any { it == "-rpath" })
//                    assert(binary.linkerOpts.any { it == frameworkDir.absolutePath })
//                    assert(binary.linkerOpts.any { it == "-F${frameworkDir.absolutePath}" })
//                }
//                else -> {}
//            }
//        }
//    }
////
////    @Test
////    fun `configure should wrap exceptions with GradleException`() {
////        every { mockProject.extensions.findByName("kotlin") } returns mockKmpExtension
////        every { mockKmpExtension.appleTargets() } returns mockk {
////            every { all(any()) } throws RuntimeException("Target processing failed")
////        }
////
////        val exception = assertThrows<GradleException> {
////            linker.configure(LinkerExtension())
////        }
////
////        assert(exception.message!!.contains("Failed to configure"))
////    }
////
////    @Test
////    fun `processTarget should skip unsupported architectures`() {
////        every { mockTarget.toSentryFrameworkArchitecture() } returns emptySet()
////        every { mockTarget.name } returns "unsupportedTarget"
////
////        linker.processTarget(mockTarget, LinkerExtension())
////
////        verify {
////            mockLogger.warn("Skipping target unsupportedTarget: Unsupported architecture")
////        }
////    }
//}