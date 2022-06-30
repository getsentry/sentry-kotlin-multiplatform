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

### JavaScript

Not implemented yet. Help appreciated.

