<p align="center">
    <a href="https://sentry.io" target="_blank" align="center">
        <img src="https://sentry-brand.storage.googleapis.com/sentry-logo-black.png" width="280">
    </a>
<br/>
    <h1>Experimental Sentry SDK for Kotlin Multiplatform</h1>
</p>

This project is an experimental SDK for Kotlin Multiplatform.
This SDK is a wrapper around different platforms such as JVM, Android, iOS, macOS, watchOS, tvOS that can be used on Kotlin Multiplatform.

## Supported Platforms

 - JVM
 - Android
 - iOS
 - macOS
 - watchOS
 - tvOS

## Configure Repository

The Kotlin Multiplatform SDK is available in the Maven central repository. You can declare this repository in your build script as follows:

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
    api("io.sentry:sentry-kotlin-multiplatform:0.0.1")
  }
}
```

### Cocoa

For iOS, MacOS, tvOS or watchOS we use CocoaPods to include [sentry-cocoa](https://github.com/getsentry/sentry-cocoa) into this SDK.
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
      Sentry.init(this) {
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

Sentry().start { options in 
  options.dsn = "___DSN___"
}
```

## Debug Symbols for Apple targets

A dSYM upload is required for Sentry to symbolicate your crash logs for viewing. The symbolication process unscrambles Apple’s crash logs to reveal the function, variables, file names, and line numbers of the crash. The dSYM file can be uploaded through the sentry-cli tool or through a Fastlane action. Please visit our [sentry.io guide](https://docs.sentry.io/clients/cocoa/dsym/) to get started on uploading debug symbols.

 ## Troubleshooting

 `WARNING: CocoaPods requires your terminal to be using UTF-8 encoding.
    Consider adding the following to ~/.profile:
    export LANG=en_US.UTF-8`

  This is a known problem and can easily be fixed as described in this [Stack Overflow post](https://stackoverflow.com/a/69395720)

 ## Contribution

 Please see the [contribution guide](https://github.com/getsentry/sentry-kotlin-multiplatform/blob/main/CONTRIBUTING.md) before contributing
