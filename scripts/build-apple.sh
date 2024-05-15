#!/bin/bash

if [ -z "$1" ]; then
  echo "Please provide a gradle project name."
  exit 1
fi

PROJECT_NAME="$1"

./gradlew "macosX64Test" \
          "iosX64Test" \
          "watchosX64Test" \
          "tvosX64Test" \
          "iosSimulatorArm64Test" \
          "publishKotlinMultiplatformPublicationToMavenLocal" \
          "publishIosArm64PublicationToMavenLocal" \
          "publishIosSimulatorArm64PublicationToMavenLocal" \
          "publishIosX64PublicationToMavenLocal" \
          "publishMacosX64PublicationToMavenLocal" \
          "publishMacosArm64PublicationToMavenLocal" \
          "publishWatchosArm32PublicationToMavenLocal" \
          "publishWatchosArm64PublicationToMavenLocal" \
          "publishWatchosSimulatorArm64PublicationToMavenLocal" \
          "publishWatchosX64PublicationToMavenLocal" \
          "publishTvosArm64PublicationToMavenLocal" \
          "publishTvosSimulatorArm64PublicationToMavenLocal" \
          "publishTvosX64PublicationToMavenLocal" \
          -p "${PROJECT_NAME}"
