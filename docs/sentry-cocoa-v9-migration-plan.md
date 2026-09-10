# Sentry Cocoa v9 migration plan

Status: complete on `codex/sentry-cocoa-v9-migration`. See [validation results and coverage limits](sentry-cocoa-v9-validation.md), including the final aggregate pass and the confirmed external-consumer evidence.

Base: PR #561, commit `9a212e3b6a8cdb54f1bba328d7e00342c46ca047`. The implementation branch starts directly at this commit. Evidence and source references are in [the investigation](sentry-cocoa-v9-investigation.md).

## Scope and decisions

- Target Cocoa **9.28.0**, the release verified during the investigation. Pin this version during migration. If implementation happens after another release, review its changes before moving the target.
- Continue using the **Sentry** product through **spm4kmp**. Keep cinterop name `sentryCocoa` and Kotlin package prefix `cocoapods.Sentry` to avoid unnecessary symbol churn.
- Preserve the common KMP API where possible, including `captureUserFeedback` and its model, by adapting it to Cocoa's replacement API internally. Audit exposed Apple-native types separately.
- Required deployment minimums: iOS/tvOS 15, macOS 12, watchOS 9. Preserve higher consumer minimums.
- Treat CocoaPods as unsupported. Remove its sample from active build/CI/release validation paths. Broader deletion of historical files or published plugin APIs is a separate compatibility decision, not a prerequisite for this migration.
- Keep the current Kotlin/spm4kmp versions unless a reproduced blocker requires an update.
- Start with a KMP-only implementation. Make an upstream Cocoa change a dependency only if crash behavior cannot be preserved reliably through existing interfaces.

## Execution order

### 1. Establish the supported build and dependency configuration

Changes:

- Update `buildSrc/src/main/java/Config.kt` and the Gradle plugin's `gradle.properties` to the same Cocoa version.
- Set the four OS minimums in SDK configuration and auto-installed Swift packages in `Spm4KmpIntegration.kt`.
- Preserve existing user-defined Sentry configurations and both plugin application orders. On plugin-owned configurations, supply compatible defaults without reducing higher minimums. For user-owned packages, document required minimums rather than silently replacing their configuration.
- Identify how spm4kmp merges minimums across targets; verify the effective generated `Package.swift`, not just the Gradle configuration values.
- Raise the SPM sample's Xcode target from iOS 14.1 to 15.0.
- Remove CocoaPods sample inclusion from `settings.gradle.kts` and its commands from active Makefile/CI sample builds. Review root plugin declarations and scripts for requirements that exist only for the retired sample.
- Publish `watchosArm32` using the existing no-op implementation so consumers can keep the target without a Cocoa dependency. Skip Cocoa installation/linking and warn that reporting is disabled. Keep `arm64_32` distinct: it remains in the archive and is used by the supported watchOS device target.
- Retain the existing watchOS simulator workaround provisionally; verify whether v9 still needs it before keeping or removing it.

Gate: SwiftPM resolves the pinned release with correct minimums, and the first iOS simulator build reaches Kotlin compilation. The plugin's generated sample configuration also resolves the same version. No CocoaPods installation is needed for supported validation.

### 2. Restore Apple compilation with the v9 public API

Changes:

- Add explicit generated extension imports for SDK entry points in `SentryApple.kt`, `SentryBridge.apple.kt`, and exception-handling code.
- Adapt option reads/writes to generated getter/setter functions across shared Apple and iOS options, including screenshots, view hierarchy, replay, and callbacks.
- Move logs enablement to the top-level Cocoa option; replace `SentryStructuredLogAttribute` with `SentryAttribute`. Preserve typed values, attribute deletion, and before-send filtering.
- Replace native feedback construction with `SentryFeedback`, using source `custom`, comments as its message, and the existing event ID as `associatedEventId`. Preserve name/email. Proposed nullable-comments mapping: `comments.orEmpty()`, without inventing feedback text; verify that Cocoa accepts this and document any behavior change if it does not.
- Select the envelope-header overload explicitly, initially `id` plus `traceContext = null`, consistent with the current absence of KMP tracing support.
- Remove obsolete cinterop macro workarounds only after their removal passes generation for supported Apple families.
- Update Apple test helpers and fixtures for the same native API changes.

Gate: iOS simulator and macOS main/test sources compile. Review the generated native API before assuming any unresolved property or method needs a Cocoa change. The investigation's 98 diagnostics are a starting inventory, not a fixed completion count.

### 3. Prove exception and crash behavior — first release-blocking gate

Changes:

- Replace copied declarations for Swift-backed dependency-container/crash-reporter/debug-provider types with the real generated bindings wherever available.
- Correct the remaining private declarations to `SentryHubInternal`, `SentryClientInternal`, and `SentryDefaultThreadInspector`. Verify selectors and runtime names against 9.28.0, and retain only declarations actually required by KMP.
- Use `getDebugImagesFromCacheForThreads` or an equivalent existing hybrid API to retain native debug-image information.
- Preserve native stack-cursor conversion, thread association, cause-chain ordering, fatal-event preparation, and synchronous envelope persistence. Audit cursor allocation ownership while adapting the boundary.
- Preserve both the native uncaught-exception-handler path and the fallback path used when no handler is available. Preserve chaining of a previously installed Kotlin exception hook.
- Replace the invalid `setMonitoring:` call. First prototype selective C++ monitor control using the existing exported C function, then verify active-monitor state and cached state across disable, close, restart, and re-enable. Do not accept a successful call as proof of lifecycle correctness.
- Verify SDK metadata override and duplicate-crash suppression against the actual v9 runtime.

Validation must execute the real native boundary, not only mocks:

| Scenario | Required observation |
| --- | --- |
| Handled Kotlin exception, including nested causes | Expected exception order, handled flag, native frames/debug images, and associated thread; no process termination. |
| Unhandled Kotlin exception with native handler | Child/sample process terminates; on relaunch the event retains scope data and is delivered once. |
| Unhandled exception without native handler | Prepared envelope is synchronously persisted; relaunch delivers it with correct fatal/session semantics. |
| Callback filtering | Deliberately dropped events remain dropped, including the envelope fallback; callback mutation and scope enrichment are retained. |
| Existing Kotlin hook | Existing hook is invoked as intended, without recursive or repeated wrapping after SDK restart. |
| C++ monitoring enabled/disabled/re-enabled | Only the intended monitor changes; close/restart does not restore a stale configuration or disable other crash monitors. |
| Kotlin termination after manual capture | No second native crash event for the same failure; crash-session accounting remains correct. |

Run fatal scenarios in separate processes or simulator app launches. Use a local test transport/receiver or inspect persisted envelopes so routine verification does not send production events. Reuse existing fixtures where possible; extend the SPM sample or add a focused test fixture when a process-relaunch harness is missing.

Upstream decision: if monitor bookkeeping/lifecycle cannot be preserved reliably, implement a narrow Cocoa hybrid operation for C++ exception-monitor control with upstream lifecycle tests. Require a released version containing it before shipping KMP. If another missing selector or operation emerges, capture a minimal reproduction and request the smallest necessary hybrid API. Do not silently remove a KMP option or weaken crash behavior to finish the version bump.

Gate: the above behavior is demonstrated. Basic event capture or successful linkage alone does not pass this stage.

### 4. Validate adapters and the consumer plugin

Extend behavioral coverage for:

- Base and iOS option propagation; replay where supported; callback replacement/filtering.
- Logs disabled/enabled, all supported levels and attribute types, mutation and deletion.
- Feedback associated-event ID, optional contact fields, and null/empty comments.
- Plugin-owned package version/minimums, higher user minimums, disabled auto-install, both plugin orders, and existing target-specific/global configurations.
- Unsupported architecture diagnostics and retained watchOS simulator handling.

Build a consumer against locally produced SDK and plugin artifacts through an isolated local Maven repository. This checks the publication boundary that the composite/source sample can hide. Verify the cinterop is included in the SDK artifact, the consumer uses matching Cocoa, and native linkage does not duplicate Sentry.

Gate: focused tests pass and an actual consumer framework/app links and starts through spm4kmp.

### 5. Complete target, API, and release validation

Compile and link the retained matrix: iOS arm64/simulator arm64/x64; macOS arm64/x64; tvOS arm64/simulator arm64/x64; watchOS arm64/simulator arm64/x64. Confirm the archive-to-Kotlin target mapping for each. Execute tests on available host/simulator targets; explicitly record any device-only or unavailable-runner coverage rather than presenting compilation as execution.

Run required repository checks after the implementation stabilizes:

```sh
./gradlew spotlessApply
./gradlew detekt
./gradlew build
./gradlew apiCheck
./gradlew -p sentry-kotlin-multiplatform-gradle-plugin test
make compile
```

The Makefile/active sample configuration must already be SPM-based before the final `make compile`. Discover precise focused compile/link/test tasks from Gradle during implementation rather than assuming a library compile task verifies the linker.

Review any API dump changes deliberately, especially Apple platform-options aliases, native-event constructor signatures, and watchOS publication changing to a no-op stub. Preserve source/binary compatibility where feasible; document genuine changes instead of automatically refreshing baselines to make checks pass. Run JVM/Android regression tests as part of the required build.

Update the changelog and consumer migration documentation with the pinned Cocoa version, OS minimums, supported targets, feedback adaptation, and any Apple-native API changes. Recheck the final SPM sample configuration and required toolchain. Any commit must include the repository-required AI co-author attribution.

Gate: checks pass, supported sample builds, crash scenarios pass, and all remaining platform coverage limitations or API breaks are explicitly recorded for release review.

## Review structure and completion criteria

Implement the stages on the migration branch and package the cohesive upgrade for review. Suggested review sections are build/consumer setup, public adapters, private crash integration, behavioral tests, and migration documentation. Avoid publishing a standalone dependency bump that leaves Apple compilation or runtime behavior broken. A necessary Cocoa change should be tracked separately and made an explicit release dependency.

The migration is complete when the SDK and consumer plugin use the same supported v9 release through spm4kmp, retained targets compile and link, the crash and adapter gates pass, and release documentation accurately describes compatibility changes. The investigation remains historical evidence; record final validation results alongside this plan as implementation proceeds.
