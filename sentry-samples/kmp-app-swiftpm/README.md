# Official Kotlin SwiftPM sample

This standalone Kotlin 2.4 build uses the Sentry Gradle plugin to register the matching Cocoa package. The SDK itself is still built with Kotlin 2.2.21 and spm4Kmp. This sample is intentionally separate from the root build so its Kotlin version does not change the existing samples.

From the repository root on macOS, publish this checkout's SDK before building the sample:

```sh
./gradlew :sentry-kotlin-multiplatform:publishToMavenLocal -Dmaven.repo.local="$PWD/sentry-kotlin-multiplatform/build/sentry-local-publish"
./gradlew --max-workers=1 -p sentry-samples/kmp-app-swiftpm linkDebugFrameworkIosSimulatorArm64 linkDebugTestIosSimulatorArm64 linkDebugTestMacosArm64 linkDebugTestTvosSimulatorArm64 linkDebugTestWatchosSimulatorArm64
```

The sample deliberately resolves Sentry KMP only from that local repository to avoid mixing a released SDK with this checkout's Cocoa version. Other dependencies resolve from Maven Central. The tasks above build the framework and test executables; they do not launch simulators. Install the corresponding platform components in Xcode first. The single-worker setting avoids concurrent Xcode package resolution against the shared SwiftPM checkout.

To embed the framework in an existing Xcode app, follow [Kotlin's official SwiftPM integration instructions](https://kotlinlang.org/docs/multiplatform/multiplatform-spm-import.html), including the one-time linkage-package setup:

```sh
XCODEPROJ_PATH=/path/to/iosApp.xcodeproj ./gradlew -p sentry-samples/kmp-app-swiftpm integrateLinkagePackage
```

Add the normal Kotlin framework build/embedding integration to that app as described by Kotlin. Commit the generated linkage package, Xcode project changes, and SwiftPM lock files. Sentry auto-install registers the native dependency; it does not edit your Xcode project. Initialize Sentry with your DSN in the hosting application before calling `captureExample()`.

For an application that already declares other official SwiftPM dependencies, the default `AUTO` provider is sufficient. This minimal sample selects `SWIFT_PM` explicitly because it starts with no packages.
