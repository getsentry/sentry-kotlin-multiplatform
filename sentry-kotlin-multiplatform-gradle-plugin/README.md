# Sentry Kotlin Multiplatform Gradle Plugin

## Overview

This Gradle plugin simplifies the setup of Sentry for Kotlin Multiplatform projects.  It offers:

1. **Automatic dependency management** – Adds the `sentry-kotlin-multiplatform` library to the `commonMain` source-set (configurable).
2. **CocoaPod/SwiftPM integration** – Installs the required Cocoa SDK artefacts when the Kotlin Cocoapods plugin is applied.
3. **Cocoa framework linking (SPM)** – Configures linker flags so that Apple binaries (iOS/macOS/tvOS/watchOS) link against the Sentry Cocoa `.xcframework` that was resolved by Swift Package Manager.

## Conditional Cocoa linking

Starting with this release the plugin only performs the Cocoa framework-linking step **when at least one Apple target is present** in the KMP build:

```kotlin
kotlin {
    iosArm64()
    iosSimulatorArm64() // Linking **enabled** – Apple family
    jvm()               // Linking skipped
}
```

If the project has **no Apple targets**, the linking step is skipped entirely – even when executed on macOS:

```kotlin
kotlin {
    jvm()
    androidTarget()
    linuxX64()          // Linking skipped – non-Apple target
}
```

Nothing else changes: dependency auto-installation and Cocoapods handling continue to work as before.

## Configuration snippet

```kotlin
plugins {
    id("io.sentry.kotlin.multiplatform.gradle") version "<version>"
}

sentryKmp {
    // Optional: override default Cocoa version
    autoInstall.cocoapods.sentryCocoaVersion.set("8.53.1")

    // Optional: disable automatic linking completely
    linker.enabled.set(false)
}
```

---
For the full list of options see the [API reference](https://getsentry.github.io/sentry-kotlin-multiplatform/) or the root project README.