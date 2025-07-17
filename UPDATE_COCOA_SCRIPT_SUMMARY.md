# Update Cocoa Script Modifications Summary

## Overview
Modified the `update-cocoa.sh` script to automatically update Sentry Cocoa SDK version references in iOS sample projects, ensuring the automation workflow no longer fails due to outdated dependency versions.

## Changes Made

### Files Updated by Script
The script now updates version strings in the following additional files:

1. **CocoaPods Sample Podspec** (`sentry-samples/kmp-app-cocoapods/shared/shared.podspec`)
   - Updates the `spec.dependency 'Sentry', 'X.X.X'` line with the new version

2. **SPM Sample Xcode Project** (`sentry-samples/kmp-app-spm/iosApp.xcodeproj/project.pbxproj`)
   - Updates the `version = X.X.X;` line in the XCRemoteSwiftPackageReference section

### Dependency Lock File Updates
The script now automatically refreshes dependency lock files:

- **CocoaPods**: Runs `pod update Sentry --silent` in the CocoaPods sample directory
  - Gracefully handles missing CocoaPods installation with warning message
  - Updates `Podfile.lock` with new dependency versions

- **Swift Package Manager**: Removes `Package.resolved` files to force regeneration
  - Clears both potential locations where Xcode stores resolved packages
  - Files are automatically regenerated when Xcode next resolves dependencies

### Error Handling
- Uses portable shell commands that work on both macOS and Linux
- Validates all regex patterns before attempting replacements
- Exits with non-zero status if any file updates fail
- Provides informative warning messages for expected failures (e.g., missing framework in CocoaPods)

### Regex Patterns
- **CocoaPods**: `(spec\.dependency 'Sentry', *)'([0-9\.]+)'`
- **SPM**: `(version *= *)([0-9\.]+);`

Both patterns match semantic version numbers (e.g., `1.2.3`) and preserve surrounding formatting.

## Result
The automation workflow can now successfully update all Sentry Cocoa SDK references across the main library and sample projects without manual intervention. The script ensures that both dependency declaration files and lock files are kept in sync with the new version.