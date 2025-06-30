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

# Replace `versionName` with the given version
VERSION_NAME_PATTERN="versionName"
perl -pi -e "s/$VERSION_NAME_PATTERN=.*$/$VERSION_NAME_PATTERN=$NEW_VERSION/g" $GRADLE_FILEPATH
perl -pi -e "s/$VERSION_NAME_PATTERN=.*$/$VERSION_NAME_PATTERN=$NEW_VERSION/g" $PLUGIN_GRADLE_FILEPATH

PODSPEC_FILEPATH='sentry-kotlin-multiplatform/sentry_kotlin_multiplatform.podspec'
PODSPEC_CONTENT=$(cat $PODSPEC_FILEPATH)

PODSPEC_REGEX="('Sentry', *)'([0-9\.]+)'"

if ! [[ $PODSPEC_CONTENT =~ $PODSPEC_REGEX ]]; then
    echo "Failed to find the Cocoa version in $PODSPEC_FILEPATH"
    exit 1
fi

PODSPEC_WHOLE_MATCH=${BASH_REMATCH[0]}
PODSPEC_VAR_NAME=${BASH_REMATCH[1]}
PODSPEC_VERSION=${BASH_REMATCH[2]}

# create a new table entry in readme with NEW_VERSION and PODSPEC_VERSION in the compatibility table
# Find the line number of the last entry in the compatibility table and insert the new entry after it
README_FILE="README.md"

# We'll look for the last line that matches the table entry pattern (starts with | and contains version numbers)
LAST_TABLE_LINE=$(grep -n "^| [0-9]" $README_FILE | tail -1 | cut -d: -f1)

if [ -z "$LAST_TABLE_LINE" ]; then
    echo "Could not find the last entry in the compatibility table"
    exit 1
fi

TEMP_FILE=$(mktemp)

head -n $LAST_TABLE_LINE $README_FILE > $TEMP_FILE
echo "| $NEW_VERSION                     | $PODSPEC_VERSION            |" >> $TEMP_FILE
tail -n +$((LAST_TABLE_LINE + 1)) $README_FILE >> $TEMP_FILE

# Replace the original file with the modified content
mv $TEMP_FILE $README_FILE

echo "Added new compatibility table entry: | $NEW_VERSION | $PODSPEC_VERSION |"