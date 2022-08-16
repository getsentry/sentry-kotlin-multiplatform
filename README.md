<p align="center">
    <a href="https://sentry.io" target="_blank" align="center">
        <img src="https://sentry-brand.storage.googleapis.com/sentry-logo-black.png" width="280">
    </a>
<br/>
    <h1>Experimental Sentry SDK for Kotlin Multiplatform</h1>
</p>

This project is an experimental SDK for Kotlin Multiplatform.
This SDK is a wrapper around different platforms such as JVM, Android, Cocoa that can be used on Kotlin Multiplatform.

## Installation

Clone or fork this repo. This SDK is under construction and therefore we only currently publish it to maven local:

```sh
./gradlew publishToMavenLocal
```

### Shared Module
In your `build.gradle` of your shared module

```gradle
repositories {
  // Because we only publish to maven local
  mavenLocal()
}
```

Add this to your `commonMain` sourceSet.

```gradle
// Add this dependency to your commonMain sourceSet
dependencies {
  api("io.sentry:sentry-kotlin-multiplatform:0.0.1")
}

```

### Cocoa

For iOS, iPadOS, MacOS, tvOS or watchOS we use CocoaPods to include [sentry-cocoa](https://github.com/getsentry/sentry-cocoa) into this SDK.
One way to achieve this is to include the Sentry Cocoa SDK via the Kotlin CocoaPods extension.

```gradle
cocoapods {
  // ...
  
  // Make sure this is the same version as the one used in the SDK
  pod("Sentry", "~> 7.21.0")

  framework {
    baseName = "shared"

    // Export the SDK in order to be able to access it directly in the iOS project
    export("io.sentry:sentry-kotlin-multiplatform:0.0.1")
  }
}
```

## Initialization

There are two main strategies for initializing the SDK:
  - Platform specific initializers
  - Shared initializer

Platform specific initializers initialize the SDK directly in the target platform. The benefit is being able to customize the configuration options specific to the platforms.

Shared initializer will initialize the SDK in your shared codebase but you will use the same configuration options for all platforms. 

It is also possible to mix those two strategies based on your needs and project setup.

## Platform Specific Initializers
### Android

```Kotlin
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

## Shared Initializer

Create a Kotlin file in your commonMain e.g. `AppSetup.kt` or however you want to call it and create a function that will initialize the SDK.

```Kotlin
// The context is needed for Android initializations
fun initializeSentry(context: Context?) {
  Sentry.init(context) {
    it.dsn = "__DSN__"
  }
}
```

Now call this function in an early lifecycle stage in your platforms.

### Android
```Kotlin
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
        AppSetupKt.initializeSentry(context = nil)
        return true        
    }
}
```
