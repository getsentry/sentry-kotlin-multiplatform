#!/bin/bash

if [ -z "$1" ]; then
  echo "Please provide a gradle project name."
  exit 1
fi

PROJECT_NAME="$1"
./gradlew ":${PROJECT_NAME}:distzip" --no-daemon --stacktrace