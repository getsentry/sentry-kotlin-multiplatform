# Sentry KMP Demo App

This is a demo app for the Sentry Kotlin Multiplatform SDK that includes a native iOS app and a native Android app with shared code.

## Shared Features
 - Dependency Injection with Koin
 - ViewModels
 - Sentry Setup

## Getting Started

### DSN
If you need to change the `DSN` you can do so in the `SentrySetup.kt` file in the `shared` module.

### DSYMS for iOS
First: you need to have `sentry-cli` installed.

The build script is already configured to upload the DSYMS to Sentry. 
It's called `Sentry dsyms upload` and you can find it under `build phases`.
You only need to change the `SENTRY_AUTH_TOKEN`, `SENTRY_PROJECT` and `SENTRY_ORG` values in the script.

