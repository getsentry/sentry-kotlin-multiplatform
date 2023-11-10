#!/bin/bash

if [ -z "$1" ]; then
  echo "Please provide a gradle project name."
  exit 1
fi

PROJECT_NAME="$1"
./gr
./gradlew ":${PROJECT_NAME}:testDebugUnitTest" \
          ":${PROJECT_NAME}:testReleaseUnitTest" \
          ":${PROJECT_NAME}:publishAndroidReleasePublicationToMavenLocal" \
          ":${PROJECT_NAME}:publishJvmPublicationToMavenLocal " \
          ":${PROJECT_NAME}:publishKotlinMultiplatformPublicationToMavenLocal" \
          --no-daemon --stacktrace