# Changelog

## Unreleased

### Dependencies

- Bump Java SDK from v7.1.0 to v7.2.0 ([#177](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/177))
  - [changelog](https://github.com/getsentry/sentry-java/blob/main/CHANGELOG.md#720)
  - [diff](https://github.com/getsentry/sentry-java/compare/7.1.0...7.2.0)

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

