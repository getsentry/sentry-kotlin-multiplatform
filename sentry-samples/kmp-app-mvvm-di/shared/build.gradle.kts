plugins {
  kotlin("multiplatform")
  id("com.android.library")
}

kotlin {
  applyDefaultHierarchyTemplate()

  androidTarget()
  listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach {
    it.binaries.framework {
      baseName = "shared"
      isStatic = true
      export(project(":sentry-kotlin-multiplatform"))
    }
  }

  sourceSets {
    commonMain.dependencies {
      api(project(":sentry-kotlin-multiplatform"))
      implementation("io.insert-koin:koin-core:3.5.2-RC1")
      implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    }

    commonTest.dependencies { implementation(kotlin("test")) }

    androidMain.dependencies { implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2") }
  }
}

android {
  compileSdk = Config.Android.compileSdkVersion
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  defaultConfig { minSdk = Config.Android.minSdkVersion }
}
