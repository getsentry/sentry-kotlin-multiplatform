#!/bin/bash

if [ -z "$1" ]; then
  echo "Please provide a gradle project name."
  exit 1
fi

PROJECT_NAME="$1"

./gradlew ":${PROJECT_NAME}:macosX64Test" \
          ":${PROJECT_NAME}:iosX64Test" \
          ":${PROJECT_NAME}:watchosX64Test" \
          ":${PROJECT_NAME}:tvosX64Test" \
          ":${PROJECT_NAME}:publishKotlinMultiplatformPublicationToMavenLocal" \
          ":${PROJECT_NAME}:publishIosArm64PublicationToMavenLocal" \
          ":${PROJECT_NAME}:publishIosSimulatorArm64PublicationToMavenLocal" \
          ":${PROJECT_NAME}:publishIosX64PublicationToMavenLocal" \
          ":${PROJECT_NAME}:publishMacosX64PublicationToMavenLocal" \
          ":${PROJECT_NAME}:publishMacosArm64PublicationToMavenLocal" \
          ":${PROJECT_NAME}:publishWatchosArm32PublicationToMavenLocal" \
          ":${PROJECT_NAME}:publishWatchosArm64PublicationToMavenLocal" \
          ":${PROJECT_NAME}:publishWatchosSimulatorArm64PublicationToMavenLocal" \
          ":${PROJECT_NAME}:publishWatchosX64PublicationToMavenLocal" \
          ":${PROJECT_NAME}:publishTvosArm64PublicationToMavenLocal" \
          ":${PROJECT_NAME}:publishTvosSimulatorArm64PublicationToMavenLocal" \
          ":${PROJECT_NAME}:publishTvosX64PublicationToMavenLocal" \
          --no-daemon --stacktrace
