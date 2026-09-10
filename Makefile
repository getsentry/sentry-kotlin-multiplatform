.PHONY: all clean compile dryRelease checkFormat checkApi buildProject buildAppleSamples generateDokka detekt format stop createCoverageReports

# Keep Gradle invocations sequential, including when make is invoked with -j.
.NOTPARALLEL:

all: stop clean compile createCoverageReports

# deep clean
clean:
	./gradlew clean
	rm -rf distributions

# local deploy
dryRelease:
	./gradlew publishToMavenLocal --no-daemon --no-parallel

# Run detekt
detekt:
	./gradlew detekt

# Generate Dokka
generateDokka:
	./gradlew dokkaHtmlMultiModule

# Check API
checkApi:
	./gradlew apiCheck

# Spotless check's code
checkFormat:
	./gradlew spotlessKotlinCheck

# Spotless format's code
format:
	./gradlew spotlessApply

# Builds the project and run tests
buildProject:
	./gradlew build

# Local validation skips symbol uploads; CI retains uploads unless explicitly opted out.
SENTRY_SKIP_UPLOAD ?= $(if $(filter true 1,$(CI)),0,1)

# Build the supported Apple sample. Uses the selected Xcode or a DEVELOPER_DIR override.
buildAppleSamples:
	/usr/bin/xcodebuild -version
	touch ./sentry-samples/kmp-app-spm/iosApp/iosApp.xcconfig
	SENTRY_SKIP_UPLOAD="$(SENTRY_SKIP_UPLOAD)" xcodebuild -project ./sentry-samples/kmp-app-spm/iosApp.xcodeproj -scheme iosApp -configuration Debug -sdk iphonesimulator -arch arm64 CODE_SIGNING_ALLOWED=NO

# The Xcode build invokes embedAndSignAppleFrameworkForXcode for the SPM sample.

# Build all targets, run tests and checks api
compile: checkApi detekt buildProject buildAppleSamples

# We stop gradle at the end to make sure the cache folders
# don't contain any lock files and are free to be cached.
stop:
	./gradlew --stop

# Create coverage reports
createCoverageReports:
	./gradlew koverXmlReport
