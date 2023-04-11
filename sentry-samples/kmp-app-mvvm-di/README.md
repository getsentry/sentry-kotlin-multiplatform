# Sentry KMP Demo App

This is a demo app for the Sentry Kotlin Multiplatform SDK that includes a native iOS app and a native Android app with shared code.

## Shared Features
 - Dependency Injection with Koin
 - ViewModels
 - Sentry Setup

## Getting Started

### IDE

Install the [Kotlin Multiplatform Mobile plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile) for Android Studio.

You can use Android Studio to run both the Android and iOS sample apps.
Alternatively you can also run the iOS app directly from Xcode.

The targets are both available as `sentry-samples.kmp-app-mvvm-di.iosApp` and `sentry-samples.kmp-app-mvvm-di.androidApp`.

### Android
- Export your `ANDROID_HOME` environment variable if you haven't done already.
- You can run `./gradlew :sentry-samples:kmp-app:androidApp:assembleDebug` to compile the Android app.

### iOS

#### Cocoapods
You need Cocoapods installed on your machine.

Run `export LANG=en_US.UTF-8` to avoid encoding issues.

`pod install` will automatically run through gradle if you run the iOS app.
However, you can still run `pod install` on the iOS folder manually if you want to make sure the pods are up to date.

#### DSYMS
First you need to have `sentry-cli` installed.

Then add the following script to your `Build Phases` in Xcode and change the `org`, `project`, `auth_token` slug placeholders accordingly:
Make sure to change the placeholders correctly, otherwise the iOS app will not run.

```shell
if which sentry-cli >/dev/null; then
export SENTRY_ORG=<org_slug>
export SENTRY_PROJECT=<project_slug>
export SENTRY_AUTH_TOKEN=<auth_token>
ERROR=$(sentry-cli upload-dif "$DWARF_DSYM_FOLDER_PATH" 2>&1 >/dev/null)
if [ ! $? -eq 0 ]; then
echo "warning: sentry-cli - $ERROR"
fi
else
echo "warning: sentry-cli not installed, download from https://github.com/getsentry/sentry-cli/releases"
fi
```

### Sentry Setup
If you need to change the `DSN` or any options you can do so in the `SentrySetup.kt` file in the `shared` module.
