# Changelog

## Unreleased

### Dependencies

- Bump Cocoa SDK from v8.44.0 to v8.48.0 ([#345](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/345))
  - [changelog](https://github.com/getsentry/sentry-cocoa/blob/main/CHANGELOG.md#8480)
  - [diff](https://github.com/getsentry/sentry-cocoa/compare/8.44.0...8.48.0)

## 0.11.0

### Fixes

- [Gradle Plugin]: Architecture folder name missmatch when using SPM and framework path searching ([#320](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/320))
- [Gradle Plugin]: Check exit value when executing `xcodebuild` command ([#326](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/326))

### Dependencies

- Bump Java SDK from v7.16.0 to v7.18.1 ([#295](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/295), [#299](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/299))
  - [changelog](https://github.com/getsentry/sentry-java/blob/main/CHANGELOG.md#7181)
  - [diff](https://github.com/getsentry/sentry-java/compare/7.16.0...7.18.1)
- Bump Cocoa SDK from v8.38.0 to v8.44.0 ([#321](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/321))
  - [changelog](https://github.com/getsentry/sentry-cocoa/blob/main/CHANGELOG.md#8440)
  - [diff](https://github.com/getsentry/sentry-cocoa/compare/8.38.0...8.44.0)

## 0.10.0

### Features

- Add experimental session replay options to common code ([#275](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/275))
```kotlin
Sentry.init { options ->
  // Adjust these values for production
  options.experimental.sessionReplay.onErrorSampleRate = 1.0
  options.experimental.sessionReplay.sessionSampleRate = 1.0
}
```
- Add `Sentry.isEnabled()` API to common code ([#273](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/273))
- Add `enableWatchdogTerminationTracking` in common options ([#281](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/281))
- Add `diagnosticLevel` in common options ([#287](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/287))

### Dependencies

- Bump Cocoa SDK from v8.36.0 to v8.38.0 ([#279](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/279), [#285](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/285))
  - [changelog](https://github.com/getsentry/sentry-cocoa/blob/main/CHANGELOG.md#8380)
  - [diff](https://github.com/getsentry/sentry-cocoa/compare/8.36.0...8.38.0)
- Bump Java SDK from v7.14.0 to v7.16.0 ([#284](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/284), [#289](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/289))
  - [changelog](https://github.com/getsentry/sentry-java/blob/main/CHANGELOG.md#7160)
  - [diff](https://github.com/getsentry/sentry-java/compare/7.14.0...7.16.0)

## 0.9.0

### Improvements

- Improve interop with objc headers ([#265](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/265))
- Plugin: dont use `latest.release` as default for the KMP dependency ([#262](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/262))

### Dependencies

- **Gradle Plugin:** Bump default Cocoa SDK from v8.26.0 to v8.36.0 ([#261](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/261))
  - [changelog](https://github.com/getsentry/sentry-cocoa/blob/main/CHANGELOG.md#8360)
  - [diff](https://github.com/getsentry/sentry-cocoa/compare/8.26.0...8.36.0)

## 0.8.0

### Features

- New Sentry KMP Gradle plugin ([#230](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/230))
  - Install via `plugins { id("io.sentry.kotlin.multiplatform.gradle") version "{version}" }`
  - Enables auto installing of the KMP SDK to `commonMain` (if all targets are supported)
    - This also automatically installs the Sentry Android SDK
  - Enables auto installing of the required Sentry Cocoa SDK with Cocoapods (if Cocoapods plugin is enabled)
  - Configures linking for SPM (needed if you want to compile a dynamic framework with `isStatic = false`)
  - Configure via the `sentryKmp` configuration block in your build file
```kotlin
// Example configuration in build.gradle.kts
sentryKmp {
  // Disable auto installing the KMP SDK to commonMain
  autoInstall.commonMain.enabled = false
} 
```

### Dependencies

- Bump Kotlin version from v1.9.21 to v1.9.23 ([#250](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/250))
- Bump Java SDK from v7.13.0 to v7.14.0 ([#253](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/253))
  - [changelog](https://github.com/getsentry/sentry-java/blob/main/CHANGELOG.md#7140)
  - [diff](https://github.com/getsentry/sentry-java/compare/7.13.0...7.14.0)
- Bump Cocoa SDK from v8.33.0 to v8.36.0 ([#258](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/258))
  - [changelog](https://github.com/getsentry/sentry-cocoa/blob/main/CHANGELOG.md#8360)
  - [diff](https://github.com/getsentry/sentry-cocoa/compare/8.33.0...8.36.0)

## 0.8.0-beta.1

### Features

- New Sentry KMP Gradle plugin ([#230](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/230))
  - Install via `plugins { id("io.sentry.kotlin.multiplatform.gradle") version "{version}" }`  
  - Enables auto installing of the KMP SDK to commonMain (if all targets are supported)
  - Enables auto installing of the required Sentry Cocoa SDK with Cocoapods (if Cocoapods plugin is enabled)
  - Configures linking for SPM (needed if you want to compile a dynamic framework)
  - Configure via the `sentryKmp` configuration block in your build file

### Dependencies

- Bump Kotlin version from v1.9.21 to v1.9.23 ([#250](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/250))
- Bump Java SDK from v7.9.0 to v7.13.0 ([#236](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/236), [#242](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/242), [#248](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/248))
  - [changelog](https://github.com/getsentry/sentry-java/blob/main/CHANGELOG.md#7130)
  - [diff](https://github.com/getsentry/sentry-java/compare/7.9.0...7.13.0)
- Bump Cocoa SDK from v8.26.0 to v8.33.0 ([#244](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/244), [#251](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/251))
  - [changelog](https://github.com/getsentry/sentry-cocoa/blob/main/CHANGELOG.md#8330)
  - [diff](https://github.com/getsentry/sentry-cocoa/compare/8.26.0...8.33.0)

## 0.7.1

### Fixes

- Symbol multiply defined when trying to run cocoa targets ([#226](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/226))

## 0.7.0

### Features

- Allow initializing the KMP SDK with native options ([#221](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/221))
  - This allows you to initialize the SDK with platform-specific options that may not be available in the common code of the KMP SDK yet.

Usage:
```kotlin
// build.gradle.kts
kotlin {
    sourceSets {
        all {
          languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }
    }
}

// commonMain
fun init() {
  Sentry.initWithPlatformOptions(createPlatformOptions())
}

expect fun platformOptionsConfiguration(): PlatformOptionsConfiguration

// iOS
actual fun createPlatformOptions(): PlatformOptionsConfiguration = { 
    dsn = "your_dsn"
    release = "1.0.0"
    // ...
}

// Android
actual fun createPlatformOptions(): PlatformOptionsConfiguration = {
  dsn = "your_dsn"
  release = "1.0.0"
  // ...
}
```

### Dependencies

- Bump Java SDK from v7.8.0 to v7.9.0 ([#219](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/219))
  - [changelog](https://github.com/getsentry/sentry-java/blob/main/CHANGELOG.md#790)
  - [diff](https://github.com/getsentry/sentry-java/compare/7.8.0...7.9.0)
- Bump Cocoa SDK from v8.25.0 to v8.26.0 ([#222](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/222))
  - [changelog](https://github.com/getsentry/sentry-cocoa/blob/main/CHANGELOG.md#8260)
  - [diff](https://github.com/getsentry/sentry-cocoa/compare/8.25.0...8.26.0)

## 0.6.0

**Note**: This release includes a bump to Sentry Cocoa v8.25.0.
Please use at least version 8.25.0 of the Sentry Cocoa SDK starting from this release.
If you are using the Cocoapods gradle plugin you need to adjust your configuration:

```kotlin
pod("Sentry") {
    version = "8.25.0"
    // These extra options are required
    extraOpts += listOf("-compiler-option", "-fmodules")
}
```

### Fixes

- Don't crash app when `applicationContext` is not available ([#217](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/217))

### Enhancements

- Make `setSentryUnhandledExceptionHook` public ([#208](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/208))

### Dependencies

- Bump Java SDK from v7.4.0 to v7.8.0 ([#205](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/205), [#206](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/206))
  - [changelog](https://github.com/getsentry/sentry-java/blob/main/CHANGELOG.md#780)
  - [diff](https://github.com/getsentry/sentry-java/compare/7.4.0...7.8.0)
- Bump Cocoa SDK from v8.20.0 to v8.25.0 ([#209](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/209))
  - [changelog](https://github.com/getsentry/sentry-cocoa/blob/main/CHANGELOG.md#8250)
  - [diff](https://github.com/getsentry/sentry-cocoa/compare/8.20.0...8.25.0)

## 0.5.0

### Features

- Add App Hang Tracking / ANR options ([#187](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/187))
  - Use `isAnrEnabled` and `anrTimeoutIntervalMillis` to configure ANR tracking for Android
  - Use `enableAppHangTracking` and `appHangTimeoutIntervalMillis` to configure App Hang tracking for iOS
  - Both options are enabled by default
- Add isCrashedLastRun ([#186](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/186))
  - You can use it with `Sentry.isCrashedLastRun()`

### Dependencies

- Bump Cocoa SDK from v8.17.2 to v8.20.0 ([#180](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/180), [#182](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/182))
  - [changelog](https://github.com/getsentry/sentry-cocoa/blob/main/CHANGELOG.md#8200)
  - [diff](https://github.com/getsentry/sentry-cocoa/compare/8.17.2...8.20.0)
- Bump Java SDK from v7.1.0 to v7.4.0 ([#177](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/177), [#181](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/181), [#189](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/189))
  - [changelog](https://github.com/getsentry/sentry-java/blob/main/CHANGELOG.md#740)
  - [diff](https://github.com/getsentry/sentry-java/compare/7.1.0...7.4.0)

## 0.4.0

### Dependencies

- Bump Java SDK from v6.33.1 to v7.1.0 ([#157](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/157), [#164](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/164))
  - [changelog](https://github.com/getsentry/sentry-java/blob/main/CHANGELOG.md#710)
  - [diff](https://github.com/getsentry/sentry-java/compare/6.33.1...7.1.0)
- Bump Cocoa SDK from v8.4.0 to v8.17.1 ([#158](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/163))
    - [changelog](https://github.com/getsentry/sentry-cocoa/blob/main/CHANGELOG.md#8172)
    - [diff](https://github.com/getsentry/sentry-cocoa/compare/8.4.0...8.17.2)
- Bump Kotlin version from v1.8.0 to v1.9.21 ([#146](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/146)

## 0.3.0

### Features

- Add sample & trace rate configuration ([#144](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/144))
- Remove need for context in Sentry.init for Android ([#117](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/117))

### Dependencies

- Bump Java SDK from v6.14.0 to v6.33.1 ([#139](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/139))
  - [changelog](https://github.com/getsentry/sentry-java/blob/main/CHANGELOG.md#6331)
  - [diff](https://github.com/getsentry/sentry-java/compare/6.14.0...6.33.1)

## 0.2.1

### Fixes

- fix: beforeBreadcrumb discarding if hook is not set ([#105](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/105))

## 0.2.0

### Features

- feat: automatically disable `io.sentry.auto-init` ([#93](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/93))

### Fixes

- fix: NSNumber to Kotlin Long crash during SentryException conversion ([#92](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/92))

### Improvements

- ref: improve samples & add SPM docs ([#82](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/82))

## 0.1.1

### Fixes 

- fix: beforeSend dropping events if not set in options ([#79](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/79))

## 0.1.0

### Features

- feat: beforeSend / fingerprinting ([#70](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/70))
- feat: configuring http client errors for Apple targets ([#76](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/76))
- feat: improve Objc/Swift experience with @HiddenFromObjc ([#62](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/62))
- feat: add view hierarchy ([#53](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/53))

### Fixes

- ref: use explicit api & add code consistency ([#63](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/63))
- fix: cocoa crash handling due to sdkInfo removal in cocoa sdk ([#68](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/68))

## 0.0.3

### Fixes

- fix: crash on Android API levels 23 and below ([#61](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/61))

## 0.0.2

### Various fixes & improvements

- remove other sdks in .craft.yml (#58) by @buenaflor
- fix: sdk name (#57) by @buenaflor
- chore: update readme (#56) by @buenaflor
- bump craft minVersion to 1.2.1 (#55) by @buenaflor

## 0.0.1-alpha.2

### Features

 - JVM, Android, iOS, macOS, watchOS, tvOS integration
 - Sentry init and close
 - Capture Message
 - Capture Exception with proper stack traces
 - Custom unhandled exception handler on Cocoa to properly catch crashes and the stacktrace
 - Scope configuration globally and locally
 - User Feedback
 - Attachments to Scope
 - Screenshots option for Android and iOS
 - Add beforeBreadcrumb hook
 - Kotlin Multiplatform Sample project

