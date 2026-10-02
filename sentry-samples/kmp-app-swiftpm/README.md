# Official Kotlin SwiftPM sample

Standalone Kotlin 2.4 build where the Sentry plugin installs Sentry Cocoa through Kotlin's official SwiftPM import. It needs no `sentryKmp` configuration.

It resolves Sentry KMP only from this checkout, so publish the SDK first (macOS, from the repository root):

```sh
./gradlew :sentry-kotlin-multiplatform:publishToMavenLocal -Dmaven.repo.local="$PWD/sentry-kotlin-multiplatform/build/sentry-local-publish"
./gradlew --max-workers=1 -p sentry-samples/kmp-app-swiftpm linkDebugFrameworkIosSimulatorArm64 linkDebugTestIosSimulatorArm64
```
