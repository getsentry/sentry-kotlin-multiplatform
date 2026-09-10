#!/bin/bash

# ./scripts/bump-version.sh <old version> <new version>
# eg ./scripts/bump-version.sh "6.0.0-alpha.1" "6.0.0-alpha.2"

set -eux

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $SCRIPT_DIR/..

OLD_VERSION="$1"
NEW_VERSION="$2"

GRADLE_FILEPATH="gradle.properties"
PLUGIN_GRADLE_FILEPATH="sentry-kotlin-multiplatform-gradle-plugin/gradle.properties"

VERSION_NAME_PATTERN="versionName"
perl -pi -e "s/$VERSION_NAME_PATTERN=.*$/$VERSION_NAME_PATTERN=$NEW_VERSION/g" $GRADLE_FILEPATH
perl -pi -e "s/$VERSION_NAME_PATTERN=.*$/$VERSION_NAME_PATTERN=$NEW_VERSION/g" $PLUGIN_GRADLE_FILEPATH

CONFIG_FILEPATH='buildSrc/src/main/java/Config.kt'
CONFIG_CONTENT=$(cat $CONFIG_FILEPATH)

COCOA_VERSION_REGEX='sentryCocoaVersion *= *"([0-9\.]+)"'

if ! [[ $CONFIG_CONTENT =~ $COCOA_VERSION_REGEX ]]; then
    echo "Failed to find the Cocoa version in $CONFIG_FILEPATH"
    exit 1
fi

COCOA_VERSION=${BASH_REMATCH[1]}

# Append the SDK/Cocoa compatibility entry unless it already exists.
README_FILE="README.md"

EXISTING_ENTRY=$(grep "| $NEW_VERSION" $README_FILE 2>/dev/null | grep "$COCOA_VERSION" 2>/dev/null || true)

if [ -n "$EXISTING_ENTRY" ]; then
    echo "Found same KMP and Cocoaversion in the compatibility table. Skipping addition to avoid duplicate."
    exit 0
fi

LAST_TABLE_LINE=$(grep -n "^| [0-9]" $README_FILE | tail -1 | cut -d: -f1)

if [ -z "$LAST_TABLE_LINE" ]; then
    echo "Could not find the last entry in the compatibility table"
    exit 1
fi

TEMP_FILE=$(mktemp)

head -n $LAST_TABLE_LINE $README_FILE > $TEMP_FILE
echo "| $NEW_VERSION                     | $COCOA_VERSION            |" >> $TEMP_FILE
tail -n +$((LAST_TABLE_LINE + 1)) $README_FILE >> $TEMP_FILE

mv $TEMP_FILE $README_FILE

echo "Added new compatibility table entry: | $NEW_VERSION | $COCOA_VERSION |"
