<p align="center">
    <a href="https://sentry.io" target="_blank" align="center">
        <img src="https://sentry-brand.storage.googleapis.com/sentry-logo-black.png" width="280">
    </a>
<br/>
    <h1>Experimental Sentry SDK for Kotlin Multiplatform</h1>
</p>

This project is an experimental SDK for Kotlin Multiplatform. It was built during Hackweek and is not finished.
This SDK is a wrapper around different platforms as JVM, Android, Cocoa, and JavaScript, that can be used on Kotlin Multiplatform.

## Installation and Usage

Clone or fork this repo. This SDK is under construction and therefore we only publish it to maven local:

```sh
./gradlew publishToMavenLocal
```

Add this to your Gradle

```gradle
repositories {
  // Because we only publish to maven local
  mavenLocal()
}

dependencies {
  implementation("io.sentry.kotlin.multiplatform:sentry-kotlin-multiplatform:0.0.1")
}

```

### Cocoa

For iOS, iPadOS, MacOS, tvOS or watchOS we use CocoaPods to include [sentry-cocoa](https://github.com/getsentry/sentry-cocoa) into this SDK.
The infrastracture of Kotlin [can't resolve CocoaPods](https://kotlinlang.org/docs/reference/native/cocoapods.html#current-limitations) yet.
Hence, when publishing and including this SDK via Maven we a different solution to package Sentry into our apps.

One way to achieve this is to include the Kotlin Multiplatform library via CocoaPods into your Xcode project and also add Sentry to the Podfile. This way CocoaPods and Xcode take care of packaging Sentry into your app.
First, you need to install [CocoaPods and the Gradle Plugin](https://github.com/JetBrains/kotlin-native/blob/master/COCOAPODS.md#install-the-cocoapods-dependency-manager-and-plugin).
Next [integrate the Kotlin Gradle project as a CocoaPods dependency](https://github.com/JetBrains/kotlin-native/blob/master/COCOAPODS.md#use-a-kotlin-gradle-project-as-a-cocoapods-dependency).
Finally, also add Sentry as a CocoaPods dependency. Your Podfile should look something like this:

```ruby
target 'iosApp' do
  use_frameworks!
  platform :ios, '13.5'
  # Pods for iosApp
  pod 'kotlin_library', :path => '../kotlin-library'
  pod 'Sentry', '~> 5.2.0' # Same version as in this SDK
end
```

Please make sure to specify the same version as used in this SDK. Otherwise weird bugs could happen.

### Android

The initialization of the Android SDK needs a context. ContextProvider takes care of passing resolving the context.
Add the following to your Application class.

```Kotlin
class YourApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        ContextProvider.init { this }
    }
}
```
