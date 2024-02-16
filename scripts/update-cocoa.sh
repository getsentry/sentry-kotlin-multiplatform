#!/bin/bash

cd $(dirname "$0")/../

config_file='buildSrc/src/main/java/Config.kt'
podspec_file='sentry-kotlin-multiplatform/sentry_kotlin_multiplatform.podspec'
podspec2_file='sentry-samples/kmp-app-cocoapods/shared/shared.podspec'
project_file='sentry-samples/kmp-app-spm/iosApp.xcodeproj/project.pbxproj'

config_content=$(cat $config_file)
podspec_content=$(cat $podspec_file)
podspec2_content=$(cat $podspec2_file)
project_content=$(cat $project_file)

config_regex='(sentryCocoaVersion *= *)"([0-9\.]+)"'
podspec_regex="('Sentry', *)'([0-9\.]+)'"

if ! [[ $config_content =~ $config_regex ]]; then
    echo "Failed to find the Cocoa version in $config_file"
    exit 1
fi

config_whole_match=${BASH_REMATCH[0]}
config_var_name=${BASH_REMATCH[1]}
config_version=${BASH_REMATCH[2]}

project_regex="(version = )(8.17.1)"

if ! [[ $podspec_content =~ $podspec_regex ]]; then
    echo "Failed to find the Cocoa version in $podspec_file"
    exit 1
fi

podspec_whole_match=${BASH_REMATCH[0]}
podspec_var_name=${BASH_REMATCH[1]}

if ! [[ $project_content =~ $project_regex ]]; then
    echo "Failed to find the Cocoa version in $project_file"
    exit 1
fi

project_whole_match=${BASH_REMATCH[0]}
project_var_name=${BASH_REMATCH[1]}

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

    newValue="${podspec_var_name}'$2'"
    echo $newValue
    echo "${project_content/${podspec_whole_match}/$newValue}" >$podspec2_file

    newValue="${project_var_name}$2"
    echo $newValue
    echo "${project_content/${project_whole_match}/$newValue}" >$project_file
    ;;
*)
    echo "Unknown argument $1"
    exit 1
    ;;
esac