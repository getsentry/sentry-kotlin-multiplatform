# CocoaPods → spm4Kmp Migration Plan & Progress

**Goal:** Migrate only the internal Apple build of the Sentry Kotlin Multiplatform SDK from the Kotlin
CocoaPods plugin to [`spm4Kmp`](https://github.com/frankois944/spm4Kmp), with **zero public API/ABI change**
and **no impact on existing consumers** (CocoaPods or SPM).

**Status legend:** `[x]` done · `[~]` in progress · `[ ]` not started

---

## Scope: internal-only, no public breaking change

- The KMP SDK is distributed via Maven (klibs), not published to CocoaPods. The committed
  `sentry_kotlin_multiplatform.podspec` is Kotlin-CocoaPods-plugin-generated internal/sample tooling, not a
  public artifact, so removing it is internal cleanup.
- Consumer-facing integration stays intact: the Gradle plugin's CocoaPods auto-install
  (`installSentryForCocoapods`) and the SPM linker path (`CocoaFrameworkLinker`) are **not** touched.

### Hard compatibility contract (definition of done)
- `cocoapods.Sentry.*` imports and the `SentryPlatformOptions` typealias must still resolve to the same
  symbols (achieved via `packageDependencyPrefix = "cocoapods"`).
- Published klib symbols are unchanged, so both the CocoaPods and SPM samples keep building untouched.
- If either cannot be preserved → stop and escalate (would turn an internal change into a public one).

---

## Phase 0 — Toolchain upgrade (prerequisite) ✅ COMPLETE (core SDK + plugin)

Required because both `spm4Kmp` and the installed **Xcode 26** need Kotlin 2.2.20+/Gradle 8.12+, and
Kotlin **2.2.21** is the first version whose cinterop commonizer supports the Xcode 26 Metal SDK headers
(Kotlin 2.2.20 crashes with `Unresolved classifier: platform/Metal/MTLLogicalToPhysicalColorAttachmentMap`).

- [x] Confirm toolchain compatibility / decide target versions
- [x] Establish current build baseline (Gradle runs on JDK 17; CI uses JDK 17)
- [x] Determine a compatible version set
- [x] Bump core versions: **Kotlin 2.2.21**, **Gradle 8.13**, **AGP 8.7.3**, `compileSdk` 35
- [x] Bump plugins: vanniktech-publish `0.30.0`, kover `0.9.1`, detekt `1.23.8`, dokka `1.9.20`,
      buildkonfig `0.17.1`, spotless `7.0.2`; serialization `1.7.3`, coroutines `1.9.0`
- [x] Migrate vanniktech publish DSL → `RELEASE_SIGNING_ENABLED=false` Gradle property (root + plugin)
- [x] Migrate kover 0.9 DSL (removed `koverReport { defaults { mergeWith("release") } }`)
- [x] AGP 8 `namespace` migration for all `android {}` blocks + strip `package` from manifests
      (SDK + both samples' shared/androidApp)
- [x] Re-enable `buildFeatures { buildConfig = true }` to preserve the Android public API (`apiCheck`)
- [x] `kotlinOptions` → `compilerOptions` (SDK + plugin)
- [x] Explicit-API return type fix (`SentryKMP.captureUserFeedback(): Unit`)
- [x] Fix no-op stub targets (js/wasmJs/linux/mingw) test wiring under Kotlin 2.2.20's new shared `web`
      source set: exclude Ktor from their test classpaths + disable their test compile/run tasks
- [x] Suppress KGP Xcode-compat warning (`kotlin.apple.xcodeCompatibility.nowarn=true`)
- [x] ktlint 1.x reformat via `spotlessApply` (~160 files) + `.editorconfig` keeping repo conventions
      (`function-naming`, `property-naming` disabled)
- [x] Regenerate detekt baseline for detekt 1.23.8 + reformatted code
- [x] Fix Cocoa log-level cinterop type change in `SentryLogConvertersTest` (use `convert`)
- [x] Fix tv/watch/macOS test wrapper missing `ApplePlatformOptions` supertype
- [x] **Verified green:** `:sentry-kotlin-multiplatform:build` (all targets + ~341 tests),
      `:sentry-kotlin-multiplatform-gradle-plugin:test`, `apiCheck`, `detekt`, `spotlessApply`

### Phase 0 remaining ✅ COMPLETE
- [x] **Samples build** on the upgraded toolchain (`make buildAppleSamples` steps):
      - [x] `./gradlew build -p sentry-samples` green (Android + JVM/Compose desktop + iOS klibs,
        both CocoaPods and SPM samples) — no Compose bump needed (`Config.composePluginVersion = 1.9.0`
        already Kotlin 2.2.x-compatible)
      - [x] `:sentry-samples:kmp-app-cocoapods:shared:podInstall` + `pod update` (Sentry Cocoa
        `8.58.2`, CocoaPods 1.16.2)
      - [x] `xcodebuild` CocoaPods iOS sample (Debug, iphonesimulator, arm64) → **BUILD SUCCEEDED**
        on Xcode 26.1.1
      - Note: `sudo xcode-select --switch` step skipped — Xcode 26.1.1 already selected. Only benign
        warnings (deployment-target 11.0 vs 12.0+ range; run-script-without-outputs).

---

## Phase 1 — spm4Kmp migration (the goal)

- [ ] **Proof-of-concept spike** (one simulator target, throwaway): prove `cocoapods.Sentry.*` import
      parity via `packageDependencyPrefix = "cocoapods"` **and** that the fragile cross-cinterop casts still
      compile/resolve:
      - `(it as InternalSentryEvent).isFatalEvent` and `storeEnvelope(envelope as objcnames.classes.SentryEnvelope)`
        in `nsexception/SentryUnhandledExceptions.kt`
      - `scope as? Internal.Sentry.SentryScope` in `CocoaScopeProvider.kt`
      - If parity holds → proceed; if not → re-scope (single cinterop / Swift `@objc` bridge).
- [ ] **SDK Gradle migration:** replace the `cocoapods {}` block in
      `sentry-kotlin-multiplatform/build.gradle.kts` with `spm4Kmp` (`io.github.frankois944.spmForKmp`) +
      `swiftPackageConfig {}` for all Apple targets; remote Swift package on
      `https://github.com/getsentry/sentry-cocoa.git` at `Config.Libs.sentryCocoaVersion`, product `Sentry`,
      `exportToKotlin = true`; port deployment targets and the KT-41709 `-D...Unavailable` macro workaround.
      Confirm `spm4Kmp` supports Kotlin 2.2.21 (requires 2.2.20+).
- [ ] **Private `Sentry.Internal` cinterop:** keep `cinterops.create("Sentry.Internal")` for all Apple
      targets and wire include/compiler paths to the Sentry Cocoa headers resolved by SwiftPM (instead of the
      CocoaPods header layout). Fallback: `remoteBinary`/checked-in package wrapper.
- [ ] **API/behavior verification:** compile all Apple targets + `appleTest`; keep the **CocoaPods sample
      building unchanged** as the klib-symbol-parity regression check.
      Note: `apiCheck` covers android/jvm only — Apple coverage relies on compile/test/sample gates.
- [ ] Keep SDK-reported telemetry `SENTRY_COCOA_PACKAGE_NAME = "cocoapods:sentry-cocoa"` unchanged
      (observable behavior; out of scope to relabel).

---

## Phase 2 — Samples, CI, cleanup

- [ ] Make the SPM sample the primary `spm4Kmp` validation path; update `Makefile` /
      `.github/workflows/kotlin-multiplatform.yml` so `buildAppleSamples` builds the SPM sample instead of
      `podInstall`/`pod update`.
- [ ] Narrow/replace `scripts/update-cocoa.sh` to update `Config.Libs.sentryCocoaVersion`, plugin metadata,
      and SPM sample lockfiles instead of the SDK podspec.
- [ ] Internal CocoaPods build tooling cleanup (SDK podspec, `generateDummyFramework`/`syncFramework`),
      **without** touching consumer-facing Gradle plugin CocoaPods support (`installSentryForCocoapods`,
      `CocoapodsAutoInstallExtension`, `CocoaFrameworkLinker`).

---

## Risks to track
- `spm4Kmp` ↔ Kotlin 2.2.21 compatibility (verify at the migration step).
- Cross-cinterop casts may not survive binding regeneration → the PoC spike de-risks this first.
- `apiCheck` does not cover Apple klibs → rely on compile/test/sample gates; the CocoaPods sample building
  unchanged is the key ABI-parity signal.
- Sentry Cocoa's SwiftPM product is an xcframework-backed binary package → validate `exportToKotlin = true`.
- WatchOS/tvOS coverage (incl. `watchosArm32`) tested explicitly.

---

## Notes
- Local builds require JDK 17 (`JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/...`); Gradle 8.13 + system Java 24
  are incompatible.
- Nothing committed yet — Phase 0 is a clean milestone and a natural commit point before Phase 1.
