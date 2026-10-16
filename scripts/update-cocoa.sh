#!/bin/bash
set -euo pipefail

cd "$(dirname "$0")/../"

case "${1:-}" in
get-repo)
    echo "https://github.com/getsentry/sentry-cocoa.git"
    exit 0
    ;;
get-version)
    ;;
set-version)
    # SemVer: no leading zeroes in numeric core or prerelease identifiers.
    numeric='(0|[1-9][0-9]*)'
    prerelease="($numeric|[0-9]*[A-Za-z-][0-9A-Za-z-]*)"
    semver="^$numeric\.$numeric\.$numeric(-$prerelease(\.$prerelease)*)?(\+[0-9A-Za-z-]+(\.[0-9A-Za-z-]+)*)?$"
    if [[ $# -ne 2 ]] || ! [[ $2 =~ $semver ]]; then
        echo "Usage: $0 set-version <semver> (for example 9.28.0)" >&2
        exit 1
    fi
    ;;
*)
    echo "Usage: $0 {get-version|get-repo|set-version <semver>}" >&2
    exit 1
    ;;
esac

config_file='buildSrc/src/main/java/Config.kt'
plugin_properties_file='sentry-kotlin-multiplatform-gradle-plugin/gradle.properties'
config_content=$(cat "$config_file")
plugin_properties_content=$(cat "$plugin_properties_file")
config_regex='(sentryCocoaVersion *= *)"([^"[:space:]]+)"'
plugin_properties_regex='(sentryCocoaVersion *= *)([^[:space:]]+)'

if ! [[ $config_content =~ $config_regex ]]; then
    echo "Failed to find the Cocoa version in $config_file" >&2
    exit 1
fi
config_whole_match=${BASH_REMATCH[0]}
config_var_name=${BASH_REMATCH[1]}
config_version=${BASH_REMATCH[2]}

if ! [[ $plugin_properties_content =~ $plugin_properties_regex ]]; then
    echo "Failed to find the Cocoa version in $plugin_properties_file" >&2
    exit 1
fi
plugin_properties_whole_match=${BASH_REMATCH[0]}
plugin_properties_var_name=${BASH_REMATCH[1]}

case "$1" in
get-version)
    echo "$config_version"
    ;;
set-version)
    checksum_regex='(sentryCocoaSha256 *= *)"([^"[:space:]]+)"'
    if ! [[ $config_content =~ $checksum_regex ]]; then
        echo "Failed to find the Cocoa checksum in $config_file" >&2
        exit 1
    fi
    checksum_whole_match=${BASH_REMATCH[0]}
    checksum_var_name=${BASH_REMATCH[1]}

    # Pin the same archive checksum as Cocoa's SwiftPM distribution, before changing any files.
    package_manifest=$(curl --fail --silent --show-error --location "https://raw.githubusercontent.com/getsentry/sentry-cocoa/$2/Package.swift")
    archive_regex='\.binaryTarget\([[:space:]]*name: "Sentry-Dynamic",[[:space:]]*url: "([^"]+)",[[:space:]]*checksum: "([0-9a-f]{64})"'
    if ! [[ $package_manifest =~ $archive_regex ]] ||
        [[ ${BASH_REMATCH[1]} != "https://github.com/getsentry/sentry-cocoa/releases/download/$2/Sentry-Dynamic.xcframework.zip" ]]; then
        echo "Failed to find the Sentry-Dynamic archive checksum for Cocoa $2" >&2
        exit 1
    fi
    checksum=${BASH_REMATCH[2]}

    new_value="${config_var_name}\"$2\""
    config_content=${config_content/"$config_whole_match"/$new_value}
    new_value="${checksum_var_name}\"$checksum\""
    config_content=${config_content/"$checksum_whole_match"/$new_value}
    new_value="${plugin_properties_var_name}$2"
    plugin_properties_content=${plugin_properties_content/"$plugin_properties_whole_match"/$new_value}
    updated_files=("$config_file" "$plugin_properties_file")
    updated_contents=("$config_content" "$plugin_properties_content")

    # Kotlin fails the Xcode build when these committed linkage packages pin a different version.
    linked_package_dir='sentry-samples/kmp-app-swiftpm/iosApp/KotlinMultiplatformLinkedPackage'
    linked_package_regex='(url: "https://github.com/getsentry/sentry-cocoa.git",[[:space:]]*exact: )"[^"]+"'
    for package_file in "$linked_package_dir/Package.swift" \
        "$linked_package_dir/subpackages/KotlinMultiplatformLinkedPackageDylib/Package.swift"; do
        package_content=$(cat "$package_file")
        if ! [[ $package_content =~ $linked_package_regex ]]; then
            echo "Failed to find the Cocoa version in $package_file" >&2
            exit 1
        fi
        new_value="${BASH_REMATCH[1]}\"$2\""
        updated_files+=("$package_file")
        updated_contents+=("${package_content/"${BASH_REMATCH[0]}"/$new_value}")
    done

    # All target files have been validated; now write the prepared replacements.
    for index in "${!updated_files[@]}"; do
        printf '%s\n' "${updated_contents[$index]}" >"${updated_files[$index]}"
    done
    ;;
esac
