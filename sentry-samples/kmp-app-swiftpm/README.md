# Official Kotlin SwiftPM sample

Standalone Kotlin 2.4 build where the Sentry plugin installs Sentry Cocoa through Kotlin's official SwiftPM import. It needs no `sentryKmp` configuration.

It resolves Sentry KMP only from this checkout. Build it from the repository root (macOS):

```sh
make buildSwiftPmSample
```

## Using it in an Xcode app

This sample has no Xcode app. In your app, link the Xcode project to the Swift package Kotlin generates for its SwiftPM dependencies, including Sentry Cocoa. Run this once:

```sh
XCODEPROJ_PATH='/path/to/iosApp/iosApp.xcodeproj' ./gradlew :shared:integrateLinkagePackage
```

Commit the generated `KotlinMultiplatformLinkedPackage` and the updated Xcode project. Kotlin keeps the package up to date afterwards, for example when the Sentry Cocoa version changes. Don't also add `sentry-cocoa` to the Xcode project yourself. See the [Kotlin docs](https://kotlinlang.org/docs/multiplatform/multiplatform-spm-import.html#run-the-swiftpm-integration-task).
