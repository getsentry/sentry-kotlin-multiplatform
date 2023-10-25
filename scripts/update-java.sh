#!/bin/bash

cd $(dirname "$0")/../
file='buildSrc/src/main/java/Config.kt'
content=$(cat $file)
regex='(sentryJavaVersion *= *)"([0-9\.]+)"'
if ! [[ $content =~ $regex ]]; then
    echo "Failed to find the Cocoa version in $file"
    exit 1
fi

case $1 in
get-version)
    echo ${BASH_REMATCH[2]}
    ;;
get-repo)
    echo "https://github.com/getsentry/sentry-java.git"
    ;;
set-version)
    newValue="${BASH_REMATCH[1]}\"$2"\"
    echo "${content/${BASH_REMATCH[0]}/$newValue}" >$file
    ;;
*)
    echo "Unknown argument $1"
    exit 1
    ;;
esac