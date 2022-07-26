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

Remember to execute the initialization as early in your application life cycle as possible.

### Android

```Kotlin
class YourApplication : Application() {
  override fun onCreate() {
    super.onCreate()
      SentryKMP.start(this) {
        it.dsn = "___DSN___"
      }
   }
}
```

### iOS
Ideally you will call this in `applicationDidFinishLaunching` in AppDelegate or in `init` in your SwiftUI App

```Swift
import shared

// ...

SentryKMP().start { options in 
  options.dsn = "___DSN___"
}
```
