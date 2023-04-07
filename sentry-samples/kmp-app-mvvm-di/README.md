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
First you need to have `sentry-cli` installed.

Then add the following script to your `Build Phases` in Xcode and change the `org`, `project`, `auth_token` slugs accordingly:

```shell
if which sentry-cli >/dev/null; then
export SENTRY_ORG=<org_slug>
export SENTRY_PROJECT=<project_slug>
export SENTRY_AUTH_TOKEN=<auth_token>
ERROR=$(sentry-cli upload-dif "$DWARF_DSYM_FOLDER_PATH" 2>&1 >/dev/null)
if [ ! $? -eq 0 ]; then
echo "warning: sentry-cli - $ERROR"
fi
else
echo "warning: sentry-cli not installed, download from https://github.com/getsentry/sentry-cli/releases"
fi
```
