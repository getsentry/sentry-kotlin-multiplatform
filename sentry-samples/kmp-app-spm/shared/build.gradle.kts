plugins {
  kotlin("multiplatform")
  id("com.android.library")
}

kotlin {
  applyDefaultHierarchyTemplate()

  androidTarget()
  jvm()
  listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach {
    it.binaries.framework {
      baseName = "shared"
      isStatic = true
      export(project(":sentry-kotlin-multiplatform"))
    }
  }

  sourceSets {
    commonMain.dependencies { api(project(":sentry-kotlin-multiplatform")) }

    commonTest.dependencies { implementation(kotlin("test")) }
  }
}

android {
  compileSdk = Config.Android.compileSdkVersion
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  defaultConfig { minSdk = Config.Android.minSdkVersion }
}
