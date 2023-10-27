#!/bin/bash

cd $(dirname "$0")/../

config_file='buildSrc/src/main/java/Config.kt'
podspec_file='sentry-kotlin-multiplatform/sentry_kotlin_multiplatform.podspec'

config_content=$(cat $config_file)
podspec_content=$(cat $podspec_file)

config_regex='(sentryCocoaVersion *= *)"([0-9\.]+)"'
podspec_regex="('Sentry', *)'([0-9\.]+)'"

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
    ;;
*)
    echo "Unknown argument $1"
    exit 1
    ;;
esac
