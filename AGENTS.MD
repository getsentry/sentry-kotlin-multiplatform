# AGENTS.md

Sentry Kotlin Multiplatform (KMP) SDK and Gradle Plugin.

## Build

- **Build tool**: Gradle via `./gradlew`
- **Format**: `./gradlew spotlessApply`
- **Lint**: `./gradlew detekt`
- **Test**: `./gradlew build` (builds and runs tests)
- **API check**: `./gradlew apiCheck` (binary compatibility)
- **Full CI**: `make compile` (runs apiCheck, detekt, build, samples)

## Quality Gate

Before committing, ensure these pass:
```bash
./gradlew spotlessApply
./gradlew detekt
./gradlew build
./gradlew apiCheck
```

## Repository Structure

```
├── sentry-kotlin-multiplatform/              # Core KMP SDK
│   └── src/
│       ├── commonMain/                       # Shared API & expect declarations
│       ├── commonJvmMain/                    # Shared JVM + Android code
│       ├── androidMain/                      # Android actual implementations
│       ├── jvmMain/                          # JVM actual implementations
│       ├── appleMain/                        # Shared Apple (iOS/macOS/tvOS/watchOS)
│       ├── iosMain/                          # iOS-specific
│       └── commonTvWatchMacOsMain/           # tvOS/watchOS/macOS-specific
├── sentry-kotlin-multiplatform-gradle-plugin/ # Optional Gradle Plugin
├── sentry-samples/                           # Sample apps (CocoaPods & SPM)
└── buildSrc/                                 # Build configuration (Config.kt)
```

## Key Conventions

- **expect/actual pattern**: Shared API in `commonMain`, platform implementations in `androidMain`, `appleMain`, `jvmMain`, etc.
- **Public API docs**: All public classes/properties in `commonMain` must have KDoc (enforced by detekt)
- **Formatting**: Spotless with ktlint — run `./gradlew spotlessApply` before committing
- **Detekt config**: `config/detekt/detekt.yml`

## Commit Attribution

AI commits MUST include:
```
Co-Authored-By: (the agent model's name and attribution byline)
```
