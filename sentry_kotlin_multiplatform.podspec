Pod::Spec.new do |spec|
    spec.name                     = 'sentry_kotlin_multiplatform'
    spec.version                  = '0.0.1'
    spec.homepage                 = 'https://github.com/getsentry/sentry-cocoa'
    spec.source                   = { :git => "Not Published", :tag => "Cocoapods/#{spec.name}/#{spec.version}" }
    spec.authors                  = ''
    spec.license                  = ''
    spec.summary                  = 'Official Sentry SDK for iOS / tvOS / macOS / watchOS'

    spec.vendored_frameworks      = "build/cocoapods/framework/sentry_kotlin_multiplatform.framework"
    spec.libraries                = "c++"
    spec.module_name              = "#{spec.name}_umbrella"

    spec.ios.deployment_target = '9.0'
    spec.osx.deployment_target = '10.10'
    spec.tvos.deployment_target = '9.0'
    spec.watchos.deployment_target = '2.0'

    spec.dependency 'Sentry', '~> 7.1.4'

    spec.script_phases = [
        {
            :name => 'Build sentry_kotlin_multiplatform',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                set -ev
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                "$REPO_ROOT/gradlew" -p "$REPO_ROOT" ::syncFramework \
                    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                    -Pkotlin.native.cocoapods.archs="$ARCHS" \
                    -Pkotlin.native.cocoapods.configuration=$CONFIGURATION \
                    -Pkotlin.native.cocoapods.cflags="$OTHER_CFLAGS" \
                    -Pkotlin.native.cocoapods.paths.headers="$HEADER_SEARCH_PATHS" \
                    -Pkotlin.native.cocoapods.paths.frameworks="$FRAMEWORK_SEARCH_PATHS"
            SCRIPT
        }
    ]
end