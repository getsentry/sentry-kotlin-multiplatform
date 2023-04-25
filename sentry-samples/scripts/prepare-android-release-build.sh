#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $DIR/../

function prop {
    grep "${1}" common-sentry.properties|cut -d'=' -f2
}

org_slug=$(prop 'org.slug')
project_slug=$(prop 'project.slug')
auth_token=$(prop 'auth.token')

sentry_props="defaults.project=$project_slug
defaults.org=$org_slug
auth.token=$auth_token"
proj_root="$PWD"

dirs=$(find "$proj_root" -type f -name "sentry.keystore" -exec dirname {} \; | grep -E 'kmp-app-(cocoapods|spm|mvvm-di)/androidApp' | sort | uniq)

for dir in $dirs; do
  if [ -d "$dir" ]; then
    # Create the sentry.properties file in the directory and write the contents to it
    echo "$sentry_props" > "$dir/sentry.properties"
    echo "Added sentry.properties to $dir"
  else
    echo "$dir does not exist"
  fi
done
