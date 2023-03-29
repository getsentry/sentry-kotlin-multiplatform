# Changelog

## 0.0.3

### Features

- Improve Objc/Swift experience with @HiddenFromObjc ([#62](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/62))

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

