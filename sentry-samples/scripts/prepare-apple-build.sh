#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $DIR/../

function prop {
    grep "${1}" common-sentry.properties|cut -d'=' -f2
}

org_slug=$(prop 'org.slug')
project_slug=$(prop 'project.slug')
auth_token=$(prop 'auth.token')

sentry_props="SENTRY_ORG = $org_slug
SENTRY_PROJECT = $project_slug
SENTRY_AUTH_TOKEN = $auth_token"
proj_root="$PWD"

dirs=$(find "$proj_root" -type f -name "Info.plist" -exec dirname {} \; | grep -E 'kmp-app-(cocoapods|spm)/iosApp' | sort | uniq)

for dir in $dirs; do
  if [ -d "$dir" ]; then
    # Create the sentry.properties file in the directory and write the contents to it
    echo "$sentry_props" > "$dir/iosApp.xcconfig"
    echo "Added xcconfig to $dir"
  else
    echo "$dir does not exist"
  fi
done
