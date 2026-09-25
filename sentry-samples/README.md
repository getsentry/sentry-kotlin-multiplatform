
# Sentry Kotlin Multiplatform Samples

This contains a Kotlin Multiplatform sample showcasing the Sentry Kotlin Multiplatform SDK on Android, iOS with Swift Package Manager, and Desktop with Jetpack Compose.

## Getting Started
> The sample modules are configured as sub-projects. Open the root project in Android Studio and sync the gradle files.

### Requirements
- Xcode (for iOS)
- Android Studio [KMM plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile)
- [sentry-cli](https://docs.sentry.io/product/cli/installation/)

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
In the shared code of the sample you will find a `SentrySetup.kt` file where you can configure the SDK to your needs.

## Running the Samples
Run configurations are automatically set through Android Studio. Further configurations are generally not needed.

## Troubleshooting
> iOS app run configuration in Android Studio is not working / shows an error

Reimporting the `kmp-app-spm/iosApp.xcodeproj` in run configurations usually fixes the problem.
It should automatically reload the project scheme and configuration.

## Further information
For more information on the Sentry Kotlin Multiplatform, please refer to the [official Sentry documentation](https://docs.sentry.io/platforms/kotlin-multiplatform/).
