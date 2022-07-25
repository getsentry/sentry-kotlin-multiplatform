plugins {
    kotlin("multiplatform")
}

kotlin {
    explicitApi()

    val macosX64 = macosX64()
    val macosArm64 = macosArm64()
    val iosArm64 = iosArm64()
    val iosX64 = iosX64()
    val iosSimulatorArm64 = iosSimulatorArm64()
    val watchosArm32 = watchosArm32()
    val watchosArm64 = watchosArm64()
    val watchosX64 = watchosX64()
    val watchosSimulatorArm64 = watchosSimulatorArm64()
    val tvosArm64 = tvosArm64()
    val tvosX64 = tvosX64()
    val tvosSimulatorArm64 = tvosSimulatorArm64()

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
    listOf(
        macosX64, macosArm64,
        iosArm64, iosX64, iosSimulatorArm64,
        watchosArm32, watchosArm64, watchosX64, watchosSimulatorArm64,
        tvosArm64, tvosX64, tvosSimulatorArm64
    ).forEach {
        it.compilations.getByName("main") {
            cinterops.create("NSExceptions.Sentry") {
                includeDirs("$projectDir/src/nativeInterop/cinterop/Sentry")
            }
        }
    }
}
