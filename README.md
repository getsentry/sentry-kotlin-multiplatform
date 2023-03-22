<p align="center">
    <picture>
      <source media="(prefers-color-scheme: dark)" srcset="https://sentry-brand.storage.googleapis.com/sentry-logo-white.png">
      <source media="(prefers-color-scheme: light)" srcset="https://sentry-brand.storage.googleapis.com/sentry-logo-black.png">
      <img alt="Shows a black logo in light color mode and a white one in dark color mode." width="280">
    </picture>
<br/>
    <h1>Experimental Sentry SDK for Kotlin Multiplatform</h1>
</p>

This project is an experimental SDK for Kotlin Multiplatform.
This SDK is a wrapper around different platforms such as JVM, Android, iOS, macOS, watchOS, tvOS that can be used on Kotlin Multiplatform.

| Packages                                | Maven Central
|-----------------------------------------| -------
| sentry-kotlin-multiplatform                          | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.sentry/sentry-kotlin-multiplatform/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.sentry/sentry-kotlin-multiplatform)

## Supported Platforms

| Target Platform | Target preset |
|:-------------:|-------------|
| Android     | <ul><li>`android`</li></ul> |
| Kotlin/JVM  | <ul><li>`jvm`</li></ul>
| iOS         | <ul><li>`iosArm64`</li><li>`iosX64`</li><li>`iosSimulatorArm64`</li></ul>|
| macOS       | <ul><li>`macosArm64`</li><li>`macosX64`</ul>|
| watchOS     | <ul><li>`watchosArm32`</li><li>`watchosArm64`</li><li>`watchosX64`</li><li>`watchosSimulatorArm64`</li></ul>|
| tvOS        | <ul><li>`tvosArm64`</li><li>`tvosX64`</li><li>`tvosSimulatorArm64`</li></ul>|

## Configure Repository

The Kotlin Multiplatform SDK is available on `mavenCentral`. You can declare this repository in your build script as follows:

```gradle
repositories {
  mavenCentral()
}
```

## Add dependency
For a multiplatform project, you need to add the sentry-kotlin-multiplatform artifact to the `commonMain` source set:

```Kotlin
val commonMain by getting {
  dependencies {
    api("io.sentry:sentry-kotlin-multiplatform:<version>")
  }
}
```

### Cocoa

If you are targeting Apple platforms (iOS, macOS, watchOS, tvOS), then you can use CocoaPods to include [Sentry Cocoa](https://github.com/getsentry/sentry-cocoa) into this SDK.
One way to achieve this is to include the Sentry Cocoa SDK via the Kotlin CocoaPods extension. Be aware that your Sentry Cocoa version has to match the version used in the KMP SDK.

```gradle
cocoapods {
  // ...
  
  // Make sure Sentry Cocoa in your project matches this version
  pod("Sentry", "~> 8.2.0")

  framework {
    baseName = "shared"

    // Export the SDK in order to be able to access it directly in the iOS project
    export("io.sentry:sentry-kotlin-multiplatform:<version>")
  }
}
```

## Initialization

There are two main strategies for initializing the SDK:
  - Shared initializer
  - Platform-specific initializers

Shared initializer will initialize the SDK in your shared codebase but you will use the same configuration options for all platforms. 

Platform-specific initializers initialize the SDK directly in the target platform. The benefit is being able to customize the configuration options specific to the platforms.

It is also possible to mix those two strategies based on your needs and project setup.

## Prerequisites (Android-only)

Both of the strategies require disabling auto-init on Android to not clash with the `ContentProvider`, which auto-initializes the Sentry Android SDK. To do so, add the following to the `AndroidManifest.xml` file under your `androidMain` source set:

```xml
<application>
    <meta-data android:name="io.sentry.auto-init" android:value="false" />
</application>
```

## Shared Initializer

Create a Kotlin file in your commonMain e.g. `AppSetup.kt` or however you want to call it and create a function that will initialize the SDK.

```Kotlin
import io.sentry.kotlin.multiplatform.Context
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.OptionsConfiguration

// The context is needed for Android initializations
fun initializeSentry(context: Context) {
  Sentry.init(context, optionsConfiguration())
}

fun initializeSentry() {
  Sentry.init(optionsConfiguration())
}

private fun optionsConfiguration(): OptionsConfiguration = {
  it.dsn = "__DSN__"
}

```

Now call this function in an early lifecycle stage in your platforms.

### Android
```Kotlin
import your.kmp.app.initializeSentry

class YourApplication : Application() {
  override fun onCreate() {
    super.onCreate()
      // Make sure to add the context!
      initializeSentry(this)
   }
}
```

### Cocoa

```Swift
import shared

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil
    ) -> Bool {
        AppSetupKt.initializeSentry()
        return true        
    }
}
```

## Platform-Specific Initializers
### Android

```Kotlin
import io.sentry.kotlin.multiplatform.Sentry

class YourApplication : Application() {
  override fun onCreate() {
    super.onCreate()
      // Make sure to add the context!
      Sentry.init(this) {
        it.dsn = "___DSN___"
      }
   }
}
```

### Cocoa
```Swift
import shared

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil
    ) -> Bool {
        Sentry.shared.doInit() { options in
          options.dsn = "__DSN__"
        }
        return true        
    }
}
```

## Debug Symbols for Apple targets

A dSYM upload is required for Sentry to symbolicate your crash logs for viewing. The symbolication process unscrambles Apple’s crash logs to reveal the function, variables, file names, and line numbers of the crash. The dSYM file can be uploaded through the sentry-cli tool or through a Fastlane action. Please visit our [sentry.io guide](https://docs.sentry.io/platforms/apple/dsym/) to get started on uploading debug symbols.

 ## Troubleshooting

 `WARNING: CocoaPods requires your terminal to be using UTF-8 encoding.
    Consider adding the following to ~/.profile:
    export LANG=en_US.UTF-8`

  This is a known problem and can easily be fixed as described in this [Stack Overflow post](https://stackoverflow.com/a/69395720)

 ## Contribution

 Please see the [contribution guide](https://github.com/getsentry/sentry-kotlin-multiplatform/blob/main/CONTRIBUTING.md) before contributing
