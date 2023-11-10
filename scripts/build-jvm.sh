#!/bin/bash

if [ -z "$1" ]; then
  echo "Please provide a gradle project name."
  exit 1
fi

PROJECT_NAME="$1"
./gr
./gradlew "testDebugUnitTest" \
          "testReleaseUnitTest" \
          "publishAndroidReleasePublicationToMavenLocal" \
          "publishJvmPublicationToMavenLocal " \
          "publishKotlinMultiplatformPublicationToMavenLocal" \
          -p "${PROJECT_NAME}" --no-daemon --stacktrace