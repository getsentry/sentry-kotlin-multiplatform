#!/bin/bash

cd $(dirname "$0")/../

config_file='buildSrc/src/main/java/Config.kt'
podspec_file='sentry-kotlin-multiplatform/sentry_kotlin_multiplatform.podspec'
plugin_properties_file='sentry-kotlin-multiplatform-gradle-plugin/gradle.properties'

# iOS sample files that need version updates
cocoapods_sample_podspec='sentry-samples/kmp-app-cocoapods/shared/shared.podspec'
spm_sample_pbxproj='sentry-samples/kmp-app-spm/iosApp.xcodeproj/project.pbxproj'

config_content=$(cat $config_file)
podspec_content=$(cat $podspec_file)
plugin_properties_content=$(cat $plugin_properties_file)

# Read iOS sample files
cocoapods_sample_content=$(cat $cocoapods_sample_podspec)
spm_sample_content=$(cat $spm_sample_pbxproj)

config_regex='(sentryCocoaVersion *= *)"([0-9\.]+)"'
podspec_regex="('Sentry', *)'([0-9\.]+)'"
plugin_properties_regex='(sentryCocoaVersion *= *)([0-9\.]+)'

# Regex patterns for iOS sample files
cocoapods_sample_regex="(spec\.dependency 'Sentry', *)'([0-9\.]+)'"
spm_sample_regex='(version *= *)([0-9\.]+);'

if ! [[ $config_content =~ $config_regex ]]; then
    echo "Failed to find the Cocoa version in $config_file"
    exit 1
fi

config_whole_match=${BASH_REMATCH[0]}
config_var_name=${BASH_REMATCH[1]}
config_version=${BASH_REMATCH[2]}

if ! [[ $podspec_content =~ $podspec_regex ]]; then
    echo "Failed to find the Cocoa version in $podspec_file"
    exit 1
fi

podspec_whole_match=${BASH_REMATCH[0]}
podspec_var_name=${BASH_REMATCH[1]}

if ! [[ $plugin_properties_content =~ $plugin_properties_regex ]]; then
    echo "Failed to find the Cocoa version in $plugin_properties_file"
    exit 1
fi

plugin_properties_whole_match=${BASH_REMATCH[0]}
plugin_properties_var_name=${BASH_REMATCH[1]}

# Check iOS sample files
if ! [[ $cocoapods_sample_content =~ $cocoapods_sample_regex ]]; then
    echo "Failed to find the Cocoa version in $cocoapods_sample_podspec"
    exit 1
fi

cocoapods_sample_whole_match=${BASH_REMATCH[0]}
cocoapods_sample_var_name=${BASH_REMATCH[1]}

if ! [[ $spm_sample_content =~ $spm_sample_regex ]]; then
    echo "Failed to find the Cocoa version in $spm_sample_pbxproj"
    exit 1
fi

spm_sample_whole_match=${BASH_REMATCH[0]}
spm_sample_var_name=${BASH_REMATCH[1]}

case $1 in
get-version)
    # We only require to return the version number of one of the files
    echo ${config_version}
    ;;
get-repo)
    echo "https://github.com/getsentry/sentry-cocoa.git"
    ;;
set-version)
    # Update the version in the config file
    newValue="${config_var_name}\"$2"\"
    echo "${config_content/${config_whole_match}/$newValue}" >$config_file

    # Update the version in the podspec file
    newValue="${podspec_var_name}'$2'"
    echo "${podspec_content/${podspec_whole_match}/$newValue}" >$podspec_file

    # Update the version in the plugin properties file
    newValue="${plugin_properties_var_name}$2"
    echo "${plugin_properties_content/${plugin_properties_whole_match}/$newValue}" >$plugin_properties_file

    # Update the version in the CocoaPods sample podspec
    newValue="${cocoapods_sample_var_name}'$2'"
    echo "${cocoapods_sample_content/${cocoapods_sample_whole_match}/$newValue}" >$cocoapods_sample_podspec

    # Update the version in the SPM sample project.pbxproj
    newValue="${spm_sample_var_name}$2;"
    echo "${spm_sample_content/${spm_sample_whole_match}/$newValue}" >$spm_sample_pbxproj

    # Update CocoaPods dependencies
    echo "Updating CocoaPods dependencies..."
    cd sentry-samples/kmp-app-cocoapods/iosApp
    if command -v pod >/dev/null 2>&1; then
        pod update Sentry --silent
        if [ $? -ne 0 ]; then
            echo "Warning: Failed to update CocoaPods dependencies"
        fi
    else
        echo "Warning: CocoaPods not found, skipping pod update"
    fi
    cd ../../../

    # Update SPM dependencies by removing and regenerating Package.resolved
    echo "Updating Swift Package Manager dependencies..."
    spm_resolved_files=(
        "sentry-samples/kmp-app-spm/iosApp.xcodeproj/.swiftpm/xcode/package.xcworkspace/xcshareddata/swiftpm/Package.resolved"
        "sentry-samples/kmp-app-spm/iosApp.xcodeproj/project.xcworkspace/xcshareddata/swiftpm/Package.resolved"
    )
    
    for resolved_file in "${spm_resolved_files[@]}"; do
        if [ -f "$resolved_file" ]; then
            rm "$resolved_file"
            echo "Removed $resolved_file"
        fi
    done
    
    # The Package.resolved files will be regenerated automatically when Xcode next resolves dependencies
    echo "Package.resolved files removed - they will be regenerated when Xcode resolves dependencies"
    ;;
*)
    echo "Unknown argument $1"
    exit 1
    ;;
esac