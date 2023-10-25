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

config_bash_rematch=$BASH_REMATCH

if ! [[ $podspec_content =~ $podspec_regex ]]; then
    echo "Failed to find the Cocoa version in $podspec_file"
    exit 1
fi

podspec_bash_rematch=$BASH_REMATCH

case $1 in
get-version)
    echo ${podspec_bash_rematch[2]}
    ;;
get-repo)
    echo "https://github.com/getsentry/sentry-cocoa.git"
    ;;
set-version)
    # Update the version in the config file
    newValue="${config_bash_rematch[1]}\"$2"\"
    echo "${config_content/${config_bash_rematch[0]}/$newValue}" >$config_file

    # Update the version in the podspec file
    newValue="${podspec_bash_rematch[1]}'$2'"
    echo "${podspec_content/${podspec_bash_rematch[0]}/$newValue}" >$podspec_file
    ;;
*)
    echo "Unknown argument $1"
    exit 1
    ;;
esac
