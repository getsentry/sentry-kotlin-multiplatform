plugins {
    kotlin("multiplatform")
    `maven-publish`
}

kotlin {
    explicitApi()

    val iosArm64 = iosArm64()
    val iosX64 = iosX64()
    val iosSimulatorArm64 = iosSimulatorArm64()

    sourceSets {
        ios()
        iosSimulatorArm64()

        val appleMain by creating
        val iosMain by getting { dependsOn(appleMain) }
        val iosSimulatorArm64Main by getting { dependsOn(appleMain) }
        val appleTest by creating {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
    listOf(
        iosArm64, iosX64, iosSimulatorArm64,
    ).forEach {
        it.compilations.getByName("main") {
            cinterops.create("NSExceptions.Sentry") {
                includeDirs("$projectDir/src/nativeInterop/cinterop/Sentry")
            }
        }
    }
}
