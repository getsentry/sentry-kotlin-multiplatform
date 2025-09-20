# Analysis of Sentry Kotlin Multiplatform Issue #341

## Issue Summary
When using `captureException()` on iOS, the captured stack trace shows where the exception was captured (site B) rather than where it was created (site A). This works correctly on Android but not on iOS.

## Root Cause Analysis

### The Core Problem
The issue stems from a fundamental difference in how exceptions are handled between iOS and Android:

1. **On Android/JVM**: When `captureException` is called, the Sentry Java SDK correctly uses the exception's original stack trace that was captured when the exception was instantiated.

2. **On iOS**: The Sentry Cocoa SDK ignores the NSException's `callStackReturnAddresses` property and instead captures the current thread's stack trace at the time `captureException` is called.

### Technical Deep Dive

#### 1. Kotlin Multiplatform's Exception Conversion
When a Kotlin `Throwable` is converted to an `NSException` for iOS, the KMP SDK does the right thing:

```kotlin
// In NSException.kt
internal fun Throwable.asNSException(appendCausedBy: Boolean = false): NSException {
    val returnAddresses = getFilteredStackTraceAddresses()
    // ... 
    return ThrowableNSException(name, getReason(appendCausedBy), returnAddresses)
}

internal class ThrowableNSException(
    name: String,
    reason: String?,
    private val returnAddresses: List<NSNumber>
) : NSException(name, reason, null) {
    override fun callStackReturnAddresses(): List<NSNumber> = returnAddresses
}
```

The `ThrowableNSException` correctly overrides `callStackReturnAddresses()` to provide the original stack trace from when the Kotlin exception was created.

#### 2. Sentry Cocoa's Exception Handling
However, in the Sentry Cocoa SDK, when `captureException` is called:

```objc
// In SentryClient.m
- (SentryEvent *)buildExceptionEvent:(NSException *)exception
{
    SentryEvent *event = [[SentryEvent alloc] initWithLevel:kSentryLevelError];
    SentryException *sentryException = [[SentryException alloc] initWithValue:exception.reason
                                                                         type:exception.name];
    event.exceptions = @[ sentryException ];
    // Note: No stack trace is extracted from the NSException here!
    return event;
}
```

Later, when preparing the event:

```objc
// In SentryClient.m, line 748-749
if (!isFatalEvent && shouldAttachStacktrace && threadsNotAttached) {
    event.threads = [self.threadInspector getCurrentThreads];
}
```

The SDK attaches the CURRENT thread's stack trace instead of using the NSException's `callStackReturnAddresses`.

#### 3. Platform-Specific Behavior
There is a special case for macOS only:

```objc
#if TARGET_OS_OSX
    if ([exception isKindOfClass:[SentryUseNSExceptionCallstackWrapper class]]) {
        event.threads = [(SentryUseNSExceptionCallstackWrapper *)exception buildThreads];
    }
#endif
```

The `SentryUseNSExceptionCallstackWrapper` class properly uses `callStackReturnAddresses` to build the stack trace, but this is only available on macOS, not iOS.

## Why This Happens

1. **Design Philosophy**: The Sentry Cocoa SDK appears to be designed primarily for capturing crashes at the point they occur, not for capturing pre-existing exceptions with their original stack traces.

2. **iOS vs macOS**: The special handling for preserving NSException stack traces exists only for macOS (`TARGET_OS_OSX`), not for iOS.

3. **Missing Integration**: There's no code in the iOS path that checks if an NSException has `callStackReturnAddresses` and uses them instead of capturing the current thread state.

## Impact

This issue significantly reduces the usefulness of error reporting on iOS for Kotlin Multiplatform applications because:
- Developers cannot see where exceptions were actually thrown
- All exceptions appear to originate from the Sentry SDK itself
- Debugging production issues becomes much more difficult

## Verification

The issue can be verified by:
1. Creating an exception at one location in the code
2. Passing it around and capturing it later
3. Observing that the stack trace shows the capture location, not the creation location

This behavior is consistent with the code analysis above and explains why the user sees Sentry frames at the top of their stack traces.

## Potential Solutions

### Solution 1: Extend iOS Support in Sentry Cocoa
The Sentry Cocoa SDK could be modified to:
1. Check if an NSException has `callStackReturnAddresses` 
2. Use those addresses to build the stack trace instead of capturing current threads
3. This would require changes similar to the macOS-only `SentryUseNSExceptionCallstackWrapper`

### Solution 2: Workaround in KMP SDK
The Sentry KMP SDK could:
1. Capture the stack trace immediately when an exception is created
2. Store it separately and attach it as additional data
3. This is less ideal as it wouldn't integrate as cleanly with Sentry's UI

### Solution 3: Use NSExceptionKt or Similar
Third-party libraries like NSExceptionKt have already solved this problem by properly handling Kotlin exceptions on iOS, but this would require additional dependencies.

## The Proper Fix

The most straightforward fix would be to modify the Sentry Cocoa SDK's `buildExceptionEvent` method to utilize the NSException's `callStackReturnAddresses` property. Here's what needs to be changed:

### Current Implementation (SentryClient.m):
```objc
- (SentryEvent *)buildExceptionEvent:(NSException *)exception
{
    SentryEvent *event = [[SentryEvent alloc] initWithLevel:kSentryLevelError];
    SentryException *sentryException = [[SentryException alloc] initWithValue:exception.reason
                                                                         type:exception.name];
    event.exceptions = @[ sentryException ];
    // Stack trace is not set here!
    return event;
}
```

### Proposed Implementation:
```objc
- (SentryEvent *)buildExceptionEvent:(NSException *)exception
{
    SentryEvent *event = [[SentryEvent alloc] initWithLevel:kSentryLevelError];
    SentryException *sentryException = [[SentryException alloc] initWithValue:exception.reason
                                                                         type:exception.name];
    
    // Check if the exception has callStackReturnAddresses
    NSArray *addresses = [exception callStackReturnAddresses];
    if (addresses && addresses.count > 0) {
        // Build a stack trace from the NSException's addresses
        SentryStacktrace *stacktrace = [self buildStacktraceFromAddresses:addresses];
        sentryException.stacktrace = stacktrace;
    }
    
    event.exceptions = @[ sentryException ];
    return event;
}
```

This would need a helper method to convert the addresses to a proper `SentryStacktrace`, similar to what's done in `SentryUseNSExceptionCallstackWrapper` for macOS.

### Alternative Approach in Sentry Cocoa

Another approach would be to extend the existing macOS-only solution to iOS by:

1. Removing the `#if TARGET_OS_OSX` condition
2. Making `SentryUseNSExceptionCallstackWrapper` available for iOS
3. Updating the capture flow to check if an NSException has valid `callStackReturnAddresses`

This would ensure that Kotlin/Native exceptions (and any other NSExceptions with proper stack traces) are correctly captured on iOS with their original stack traces.

## Summary

The issue is that Sentry Cocoa SDK on iOS ignores the NSException's `callStackReturnAddresses` property and instead captures the current thread's stack trace. This results in misleading crash reports where all exceptions appear to originate from the Sentry SDK itself rather than showing where they were actually thrown.

The fix requires modifying the Sentry Cocoa SDK to respect the NSException's stack trace when available, similar to how it's already done for macOS but not for iOS.