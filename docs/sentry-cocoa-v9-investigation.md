# Sentry Cocoa v9 upgrade investigation

Investigated on 2026-09-10. Base: [PR #561](https://github.com/getsentry/sentry-kotlin-multiplatform/pull/561), head `9a212e3b6a8cdb54f1bba328d7e00342c46ca047`. Investigation branch: `codex/sentry-cocoa-v9-investigation`.

## Recommendation

Upgrade from **8.58.2 to 9.28.0**, retaining the **Sentry** SwiftPM product and **spm4kmp** integration. The standard v9 binary can generate Kotlin bindings, and representative Kotlin calls compile against them. Switching to SentryObjC is an optional architectural change, not a demonstrated prerequisite for v9.

The upgrade requires substantial Apple adapter and private crash-interface work. **No mandatory upstream Cocoa change has been established.** Existing v9 interfaces and an exported C function provide a plausible KMP-only path. This is not an end-to-end validation of crash reporting; that must precede shipping. An upstream hybrid API for crash-monitor configuration would remove one particularly fragile dependency.

GitHub's releases API identified [9.28.0](https://github.com/getsentry/sentry-cocoa/releases/tag/9.28.0), published 2026-09-10 at 02:52 UTC, as latest. Inspected source commit: `61e8cb02434b26fb34c58126d883e5663cfde238`. Do not use `main` or an unbounded dependency range as the implementation reference.

## Changes in this repository

### 1. Version, deployment targets, and consumer installation

Update `buildSrc/src/main/java/Config.kt` and `sentry-kotlin-multiplatform-gradle-plugin/gradle.properties` together. The first versions the SDK's dependency; the second versions the consumer auto-installation. `scripts/update-cocoa.sh` already updates both.

Use the minimums from the **9.28.0 manifest**, rather than only the original v9 migration guide:

| Platform | Current SDK configuration | Required by 9.28.0 |
| --- | --- | --- |
| iOS | 11.0 | 15.0 |
| macOS | 10.13 | 12.0 |
| tvOS | 11.0 | 15.0 |
| watchOS | 4.0 | 9.0 |

Source: [versioned Package.swift](https://github.com/getsentry/sentry-cocoa/blob/9.28.0/Package.swift).

Apply these minimums to the SDK's spm4kmp configuration **and** `Spm4KmpIntegration.kt`. The consumer integration currently leaves them at spm4kmp defaults (iOS/tvOS 12, macOS 10.13, watchOS 4). Otherwise the SDK may build but consumer SwiftPM resolution/build will fail. Raise the SPM sample's Xcode deployment target from 14.1 to at least 15.0, and document the consumer requirements. Respect explicitly configured higher consumer targets.

Retain product `Sentry`, cinterop name `sentryCocoa`, and the existing `cocoapods.Sentry` Kotlin package prefix. That prefix preserves published symbol names; it does not imply CocoaPods dependency delivery. CocoaPods samples/configuration still exist in this base branch but are outside the supported migration path.

The released main archive has no armv7k watchOS slice. Revisit the declared `watchosArm32` target; distinguish it from `arm64_32`, which is present. Check every supported architecture rather than assuming the old target matrix still links. The existing watchOS simulator `aarch64` workaround also needs verification with v9.

### 2. Adapt public Apple calls

| Area | Required change |
| --- | --- |
| SDK entry points | Import generated extension functions for the `SentrySDK` methods now exposed through Objective-C categories, e.g. `startWithConfigureOptions`, `captureEvent`, `captureMessage`, `logger`, `close`. |
| Options | Check generated accessors rather than mechanically retaining property syntax. The standalone probe required `setEnableLogs(true)` and `setBeforeSend { ... }`. Apply this review across base/iOS/replay options. |
| Logs | Move enablement from `experimental().setEnableLogs(...)` to the top-level option. Replace `SentryStructuredLogAttribute` with `SentryAttribute`; preserve attribute mutation/removal and callback filtering behavior. |
| Feedback | Replace removed `SentryUserFeedback` / `captureUserFeedback` with `SentryFeedback` / `captureFeedback`. Map comments to message and the existing event ID to `associatedEventId`, not the feedback's own ID. Define the behavior for nullable comments and cover it in tests. |
| Exceptions | Cocoa exception value/type are nullable. KMP already accepts nullable values; no common-model change is required for this alone. |
| Envelope fallback | The event-ID-only header constructor no longer matches. Use an explicit overload such as `SentryEnvelopeHeader(id = preparedEvent.eventId, traceContext = null)` while tracing remains unsupported. |
| Interop workarounds | Reassess the old macro substitutions. Cocoa renamed `SentryMechanismMeta` to `SentryMechanismContext` and removed `SentryIntegrationProtocol`. The standalone v9 import succeeded without these substitutions. |

Relevant files: `SentryBridge.apple.kt`, `SentryApple.kt`, `TypeAliases.apple.kt`, Apple/iOS option extensions, log converters, feedback converter, event/exception converters, and their Apple tests. Public platform-options aliases and the public native-event constructor make binary/API checks necessary even when keeping the existing Kotlin package prefix.

### 3. Repair private exception and crash interfaces

The copied headers under `src/nativeInterop/cinterop/SentryInternal` are declarations used to generate Kotlin code, not proof that the corresponding runtime objects still implement them. They can compile successfully while producing bad links or selectors.

These differences were confirmed against the released binary with a local macOS runtime probe:

| Current assumption | v9.28.0 observation | Migration action |
| --- | --- | --- |
| Objective-C class `SentryDependencyContainer` | Runtime class is `Sentry.SentryDependencyContainer` | Prefer the real generated binding, which carries Swift's runtime name, over the copied class declaration. |
| `currentHub` / client typed as old `SentryHub` / `SentryClient` internals | Internal runtime classes are `SentryHubInternal` / `SentryClientInternal` | Update private declarations to the actual internal types; do not substitute the public Swift facade classes. |
| Client inspector typed as `SentryThreadInspector` | Client uses `SentryDefaultThreadInspector`; the Swift inspector is a different wrapper | Correct the type used for `stacktraceBuilder` access. |
| Container crash reporter typed as `SentryCrash`, with writable `monitoring` | Actual object is `Sentry.SentryCrashSwift`; getter exists, `setMonitoring:` does not | Keep the real handler binding; replace the unsupported write. |
| `getDebugImagesForThreads:` | Selector absent; `getDebugImagesFromCacheForThreads:` exists | Update the exception debug-image lookup, or use the hybrid debug API with raw addresses. |

The existing fatal-event preparation selector still exists on `SentryClientInternal`. The archive also exports `sentrycrashsc_initWithBacktrace` and `sentrycrash_setMonitoring`. Those provide a route for retaining stack-cursor conversion and selective C++ monitor disabling without a Cocoa release, but require precise private declarations and runtime tests. Calling the C function directly bypasses the Objective-C object's cached monitoring property; test restart/reconfiguration behavior rather than assuming equivalence to the old setter.

Preserve both unhandled-exception paths: invoking the crash reporter's exception handler, and synchronously storing a prepared envelope when the handler is unavailable. Validate cause chains, native addresses/debug images, scope enrichment, before-send filtering, duplicate suppression, next-launch delivery, and crash-session accounting. A compile-only migration cannot establish these behaviors.

Sources: [crash wrapper](https://github.com/getsentry/sentry-cocoa/blob/9.28.0/Sources/Swift/SentryCrash/SentryCrashSwift.swift), [debug provider](https://github.com/getsentry/sentry-cocoa/blob/9.28.0/Sources/Swift/SentryCrash/SentryDebugImageProvider.swift), [client implementation](https://github.com/getsentry/sentry-cocoa/blob/9.28.0/Sources/Sentry/SentryClient.m), [monitor C interface](https://github.com/getsentry/sentry-cocoa/blob/9.28.0/Sources/Sentry/include/SentryCrashC.h).

## Should Sentry Cocoa change?

**For the proposed minimal upgrade:** a Cocoa change is not yet proven necessary. Recommend adding a narrow hybrid API to enable/disable C++ exception monitoring, or a correctly implemented setter on the wrapper. It should preserve monitor bookkeeping and behavior across SDK restart. That is preferable to KMP reaching into the crash engine's C internals.

Longer term, expose hybrid operations for native stacktrace conversion and fatal-event preparation/storage. KMP should depend on these operations rather than reproducing the SDK's private object graph. SDK metadata and envelope-storage hybrid APIs already exist; they do not need to be invented again.

**If choosing SentryObjC instead:** expect a larger migration. `SentryObjCEvent`, scope, options, etc. wrap native objects; they are not aliases. KMP's current casts between public and private cinterop objects become invalid. An explicit conversion/exception bridge or upstream wrapper APIs would be needed to retain current semantics. Changing the native public types also affects KMP's exposed Apple API.

The 9.28.0 `SentryObjC-Static.xcframework` contains `libSentryObjC.a` and headers, with **no module map**. Direct-header Kotlin interop passed, but direct module import failed. spm4kmp 1.9.5's generator explicitly requires a module map. Adding a map to each static slice is a concrete upstream packaging improvement if this route is selected; the full spm4kmp static-product path still needs testing. Source product `SentryObjC` and dynamic product `SentryObjC-Dynamic` are alternatives to investigate. Products whose names differ from module `SentryObjC` need the plugin's product/module alias configuration. Do not independently embed both complete SDK distributions in an app.

Sources: [SentryObjC architecture](https://github.com/getsentry/sentry-cocoa/blob/9.28.0/develop-docs/SENTRY-OBJC.md), [spm4kmp module discovery](https://github.com/francois944/spm4Kmp/blob/1.9.5/plugin-build/plugin/src/main/java/io/github/frankois944/spmForKmp/tasks/apple/generateCInteropDefinition/GenerateCInteropDefinitionTask.kt).

## Validation performed and remaining work

- Kotlin/Native 2.2.21 generated iOS simulator bindings from the released standard `Sentry` module, without the repository's old macro workarounds.
- Representative Kotlin initialization, event, logging, feedback, and close calls compiled against those bindings after accessor/import adjustments. This produced a library; it did not execute or transmit events.
- SentryObjC's pure headers generated bindings in non-module mode. This does not establish spm4kmp integration or application linkage.
- A macOS executable linked the standard Sentry framework and confirmed the runtime classes and selectors listed above. It did not initialize the SDK or transmit events.
- The version-only repository build resolved 9.28.0 through SwiftPM and failed because the generated package required iOS 12 while Sentry requires iOS 15.
- Repeating `./gradlew :sentry-kotlin-multiplatform:compileKotlinIosSimulatorArm64` with the four required deployment minimums successfully built the Swift package and generated the cinterops, then failed Kotlin compilation with **98 diagnostics across eight files**. These cover SDK extension imports, option accessors, removed feedback/log attribute types, and the envelope-header overload. Several diagnostics cascade from the same removed type; this is not 98 independent migration tasks. The copied private headers still compiled, reinforcing the need for the separate runtime checks.
- Temporary version/deployment changes were reverted after the probes. This branch retains the investigation document, not an incomplete dependency bump. Build caches may contain the probe outputs.

Before release, complete Apple source adaptation, compile and link the supported target matrix, run Apple adapter tests and fatal-crash relaunch tests, and build the SPM sample through the consumer plugin. Check formatting, detekt, build, and API compatibility. Full CI and crash-delivery tests have not passed as part of this investigation.
