
# Sentry Kotlin Multiplatform Samples

This contains three samples of Kotlin Multiplatform projects showcasing the Sentry Kotlin Multiplatform SDK usage.
- Sample 1: Android, iOS with Cocoapods, Desktop with Jetpack Compose
- Sample 2: Android, iOS with Swift Package Manager, Desktop with Jetpack Compose
- Sample 3: Android with Jetpack Compose, iOS with SwiftUI and MVVM and Dependency Injection with [Koin](https://insert-koin.io/)

## Getting Started
> All samples are configured as sub-projects. Open the root project in Android Studio and sync the gradle files.

### Requirements
- Xcode (for iOS)
- Android Studio [KMM plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile)
- [sentry-cli](https://docs.sentry.io/product/cli/installation/)
- Cocoapods

### Configuration
Modify the `common-sentry.properties` file according to your needs.

```bash
# common-sentry.properties
org.slug=YOUR_ORG_SLUG  
project.slug=YOUR_PROJECT_SLUG  
auth.token=YOUR_AUTH_TOKEN
```
#### Android
Before running the Android app, execute the `./prepare-android-build.sh` script in the `.../sentry-samples/scripts` directory.
You might need to re-sync your gradle files afterwards.
By default the Android application is using the `release` build and will automatically upload proguard mapping files for deobfuscation after the setup.

#### iOS
Before running the iOS app, execute the `./prepare-apple-build.sh` script in the `.../sentry-samples/scripts` directory.
By default the iOS application will automatically upload debug symbol files for deobfuscation after the setup.

### SDK configuration
In the shared code of each sample you will find a `SentrySetup.kt` file where you can configure the SDK to your needs.

## Running the Samples
Run configurations are automatically set through Android Studio. Further configurations are generally not needed.

## Troubleshooting
> iOS app run configuration in Android Studio is not working / shows an error

Reimporting the `.xcodeproj` or `.xcworkspace` in run configurations depending on whether you run the SPM or Cocoapods sample usually fixes the problem. 
It should automatically reload the project scheme and configuration.

> WARNING: CocoaPods requires your terminal to be using UTF-8 encoding. Consider adding the following to ~/.profile:

Run `export LANG=en_US.UTF-8` to avoid encoding issues.

## Further information
For more information on the Sentry Kotlin Multiplatform, please refer to the [official Sentry documentation](https://docs.sentry.io/platforms/kotlin-multiplatform/).
