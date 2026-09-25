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
    new_value="${config_var_name}\"$2\""
    printf '%s\n' "${config_content/"$config_whole_match"/$new_value}" >"$config_file"
    new_value="${plugin_properties_var_name}$2"
    printf '%s\n' "${plugin_properties_content/"$plugin_properties_whole_match"/$new_value}" >"$plugin_properties_file"

    # The SPM sample uses the Gradle plugin's auto-install and needs no separate update.
    ;;
esac
