<p align="center">
    <picture>
      <source media="(prefers-color-scheme: dark)" srcset="https://sentry-brand.storage.googleapis.com/sentry-logo-white.png">
      <source media="(prefers-color-scheme: light)" srcset="https://sentry-brand.storage.googleapis.com/sentry-logo-black.png">
      <img alt="Shows a black logo in light color mode and a white one in dark color mode." width="280">
    </picture>
<br/>
    <h1>Sentry SDK for Kotlin Multiplatform</h1>
</p>

_Bad software is everywhere, and we're tired of it. Sentry is on a mission to help developers write
better software faster, so we can get back to enjoying technology. If you want to join
us [<kbd>**Check out our open positions**</kbd>](https://sentry.io/careers/)_

This SDK is a wrapper around different platforms such as JVM, Android, iOS, macOS, watchOS, tvOS
that can be used on Kotlin Multiplatform.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.21-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![KDoc link](https://img.shields.io/badge/API_Reference-KDoc-blue)](https://getsentry.github.io/sentry-kotlin-multiplatform/)

| Packages                    | Maven Central                                                                                                                                                                                                
|-----------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
| sentry-kotlin-multiplatform | [![Maven Central](https://maven-badges.sml.io/sonatype-central/io.sentry/sentry-kotlin-multiplatform/badge.svg?style=flat)](https://central.sonatype.com/artifact/io.sentry/sentry-kotlin-multiplatform) 
| sentry-kotlin-multiplatform-gradle-plugin | [![Maven Central](https://maven-badges.sml.io/sonatype-central/io.sentry/sentry-kotlin-multiplatform-gradle-plugin/badge.svg?style=flat)](https://central.sonatype.com/artifact/io.sentry/sentry-kotlin-multiplatform-gradle-plugin) 

## Supported Platforms

| Target Platform | Target preset                                                                                                |
|:---------------:|--------------------------------------------------------------------------------------------------------------|
|     Android     | <ul><li>`android`</li></ul>                                                                                  |
|   Kotlin/JVM    | <ul><li>`jvm`</li></ul>                                                                                      
|       iOS       | <ul><li>`iosArm64`</li><li>`iosX64`</li><li>`iosSimulatorArm64`</li></ul>                                    |
|      macOS      | <ul><li>`macosArm64`</li><li>`macosX64`</ul>                                                                 |
|     watchOS     | <ul><li>`watchosArm32`</li><li>`watchosArm64`</li><li>`watchosX64`</li><li>`watchosSimulatorArm64`</li></ul> |
|      tvOS       | <ul><li>`tvosArm64`</li><li>`tvosX64`</li><li>`tvosSimulatorArm64`</li></ul>                                 |

## Stubbed Platforms (No-Op Implementations)

You can add these targets to your Kotlin Multiplatform project just like the supported ones.  
They compile and satisfy the API surface, but **do nothing at runtime**.

| Target Platform | Target preset                                     |
|:---------------:|---------------------------------------------------|
| JS              | <ul><li>`js`</li></ul>                            |
| Wasm JS         | <ul><li>`wasmJs`</li></ul>                        |
| Linux           | <ul><li>`linuxx64`</li><li>`linuxarm64`</li></ul> |
| Windows         | <ul><li>`mingwx64`</li></ul>                      |

### Cocoa SDK Version Compatibility Table

Every version of our Kotlin Multiplatform SDK is compiled with a specific version of the [Sentry Cocoa SDK](https://github.com/getsentry/sentry-cocoa/).
Use the Kotlin Multiplatform and Cocoa SDK combinations listed in the table below to ensure the best compatibility and stability.

| Kotlin Multiplatform SDK Version | Cocoa SDK Version |
|----------------------------------| ----------------- |
| 0.6.0                            | 8.25.0            |
| 0.7.0, 0.7.1                     | 8.26.0            |
| 0.8.0, 0.9.0                     | 8.36.0            |
| 0.10.0                           | 8.38.0            |
| 0.11.0                           | 8.44.0            |
| 0.12.0                           | 8.49.0 (Xcode 16.3 compatible) |
| 0.13.0                           | 8.49.1            |
| 0.14.0                           | 8.53.1            |
| 0.15.0, 0.16.0, 0.17.0, 0.17.1, 0.18.0 | 8.53.2      |
| 0.19.0                     | 8.55.1            |
| 0.20.0                     | 8.55.1            |
| 0.21.0                     | 8.57.1            |
| 0.22.0                     | 8.57.3            |
| 0.23.0                     | 8.57.3            |
| 0.23.1                     | 8.57.3            |
| 0.24.0                     | 8.57.3            |
| 0.25.0                     | 8.57.3            |
| 0.26.0                     | 8.58.2            |
| 0.27.0                     | 8.58.2            |

## Usage

For detailed usage, check out the [Kotlin Multiplatform Documentation](https://docs.sentry.io/platforms/kotlin-multiplatform/).

### Apple linking via spm4Kmp (Gradle plugin)

If you apply the [spm4Kmp](https://github.com/frankois944/spm4Kmp) plugin (`io.github.frankois944.spmForKmp`)
alongside the Sentry Kotlin Multiplatform Gradle plugin, the matching Sentry Cocoa version is added to
your Apple targets automatically — you don't need to declare the Sentry Swift package yourself:

```kotlin
plugins {
    kotlin("multiplatform")
    id("io.sentry.kotlin.multiplatform.gradle")
    id("io.github.frankois944.spmForKmp")
}
```

You can override the version or opt out (for example if you configure the Sentry Swift package
manually):

```kotlin
sentryKmp {
    autoInstall {
        spm {
            // enabled = false // opt out of the automatic Sentry Cocoa Swift package
            // sentryCocoaVersion = "8.58.2" // override the default version
        }
    }
}
```

> [!NOTE]
> Apply this plugin **before** the spm4Kmp plugin, as in the snippet above, so that the settings
> above can go anywhere in the build script. If spm4Kmp is applied first, the Sentry Swift package
> has to be registered as the Apple targets are created, so the `sentryKmp { }` block must then come
> before the `kotlin { }` block — otherwise the opt-out (including the global `autoInstall.enabled`
> flag) and version override have no effect. The plugin warns when it detects this.

> [!IMPORTANT]
> If spm4Kmp is applied first *and* you declare the Sentry Swift package yourself inside a target
> (for example `iosArm64 { swiftPackageConfig(cinteropName = "sentryCocoa") { ... } }`), you must opt
> out as shown above. The auto-install cannot see a config declared there, so both declarations end
> up in the same spm4Kmp config and SwiftPM fails on the duplicate `sentry-cocoa` dependency.
> Applying this plugin first avoids the problem: your config is then detected and left alone.

Consumers that don't use spm4Kmp keep the existing behavior: the CocoaPods auto-install (when the
Kotlin CocoaPods plugin is applied) or the `linker { frameworkPath / xcodeprojPath }` fallback for
plain SPM-in-Xcode setups.

## Samples

For detailed information on how to build and run the samples, check out our `README.md` in the
[sentry-samples](https://github.com/getsentry/sentry-kotlin-multiplatform/tree/main/sentry-samples)
folder.

## Apple Privacy Manifest

Starting with [May 1st 2024](https://developer.apple.com/news/?id=3d8a9yyh), apps submitted to the Apple App Store are required to declare approved reasons to access certain privacy-relevant APIs. This also includes usages of these APIs via third-party SDKs. To ensure compliance, update your Sentry Cocoa SDK to `8.21.0`.
For more information, refer to our [Apple Privacy Manifest Guide](https://docs.sentry.io/platforms/kotlin-multiplatform/data-management/apple-privacy-manifest/).

## Contribution

Please see
the [contribution guide](https://github.com/getsentry/sentry-kotlin-multiplatform/blob/main/CONTRIBUTING.md)
before contributing

# Resources

* [![Kotlin Multiplatform Documentation](https://img.shields.io/badge/documentation-sentry.io-green.svg?label=documentation)](https://docs.sentry.io/platforms/kotlin-multiplatform/)
* [![Discussions](https://img.shields.io/github/discussions/getsentry/sentry-kotlin-multiplatform.svg)](https://github.com/getsentry/sentry-kotlin-multiplatform/discussions)
* [![Discord Chat](https://img.shields.io/discord/621778831602221064?logo=discord&logoColor=ffffff&color=7389D8)](https://discord.gg/PXa5Apfe7K)
* [![Stack Overflow](https://img.shields.io/badge/stack%20overflow-sentry-green.svg)](http://stackoverflow.com/questions/tagged/sentry)
* [![Code of Conduct](https://img.shields.io/badge/code%20of%20conduct-sentry-green.svg)](https://github.com/getsentry/.github/blob/master/CODE_OF_CONDUCT.md)
* [![X Follow](https://img.shields.io/twitter/follow/sentry?label=sentry&style=social)](https://x.com/intent/follow?screen_name=sentry)
