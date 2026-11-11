#!/bin/bash
set -euo pipefail

sdk=$1
target=$2
framework_dir=$3
source=$4
output_dir=$5
sdk_path=$(xcrun --sdk "$sdk" --show-sdk-path)
swiftc=$(xcrun --sdk "$sdk" --find swiftc)
swift_runtime_dir="$(dirname "$(dirname "$swiftc")")/lib/swift/$sdk"

mkdir -p "$output_dir"
object="$output_dir/SentryKMPMetrics.o"
"$swiftc" -sdk "$sdk_path" -target "$target" -F "$framework_dir" \
    -module-name SentryKMPMetricsBridge -parse-as-library -O -whole-module-optimization \
    -emit-object -emit-objc-header-path "$output_dir/SentryKMPMetrics.h" -o "$object" "$source"

# Package the compatibility archives requested by the compiler. The consumer's
# Kotlin/Native linker does not search the Swift compiler's toolchain directory.
load_commands=$(xcrun otool -l "$object")
libraries=("$object")
while IFS= read -r library; do
    libraries+=("$swift_runtime_dir/lib${library}.a")
done < <(awk '/string #[0-9]+ -lswiftCompatibility/ { print substr($3, 3) }' <<< "$load_commands" | sort -u)
xcrun libtool -static -arch_only "${target%%-*}" -o "$output_dir/libSentryKMPMetrics.a" "${libraries[@]}"
