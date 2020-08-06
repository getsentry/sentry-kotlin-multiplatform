# Contributing

This SDK still needs a lot of love.

## Cocoa

[sentry-cocoa](https://github.com/getsentry/sentry-cocoa) is included via the [CocoaPods plugin of Gradle](https://kotlinlang.org/docs/reference/native/cocoapods.html).
Libraries integrated with CocoaPods in an Kotlin Multiplatform module can only be built with Xcode, because the infrastracture of Kotlin [can't resolve CocoaPods](https://kotlinlang.org/docs/reference/native/cocoapods.html#current-limitations).
This means that we have to use an Xcode project like [ios-cocoa](./Samples/ios-cocoa) to be able to test this library on Cocoa.
Running the tests with Gradle results in the following error:

```
Undefined symbols for architecture x86_64:
  "_OBJC_CLASS_$_SentrySDK", referenced from:
      objc-class-ref in result.o
  "_OBJC_CLASS_$_SentryOptions", referenced from:
      objc-class-ref in result.o
ld: symbol(s) not found for architecture x86_64
```

**Help appreciated**: There exists the possibilty to include Objective-C frameworks with using [`cinterop`](https://kotlinlang.org/docs/reference/native/gradle_plugin.html#using-cinterop).
The advantage with this would be that sentry-cocoa is directly integrated in
the Kotlin Multiplatform library. It could happen that the CocoaPods plugin is extended to not depend on Xcode so using `cinterop` could become obsolete.

## JVM
