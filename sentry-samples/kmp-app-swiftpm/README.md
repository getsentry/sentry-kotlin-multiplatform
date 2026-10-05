# Official Kotlin SwiftPM sample

Standalone Kotlin 2.4 build where the Sentry plugin installs Sentry Cocoa through Kotlin's official SwiftPM import. It needs no `sentryKmp` configuration.

It resolves Sentry KMP only from this checkout. Build it from the repository root (macOS):

```sh
make buildSwiftPmSample
```

This sample has no Xcode app. An Xcode app also needs Kotlin's one-time linkage setup (`integrateLinkagePackage`); see the [Kotlin docs](https://kotlinlang.org/docs/multiplatform/multiplatform-spm-import.html).
