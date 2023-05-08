.PHONY: all clean compile dryRelease checkFormat format stop

all: stop clean compile

# deep clean
clean:
	./gradlew clean
	rm -rf distributions

# local deploy
dryRelease:
	./gradlew publishToMavenLocal --no-daemon --no-parallel

# Check API
checkApi:
	./gradlew apiCheck

# Spotless check's code
checkFormat:
	./gradlew spotlessKotlinCheck

# Spotless format's code
format:
	./gradlew spotlessApply

# build and run tests
compile:
	make checkApi
	./gradlew build
	make buildAppleSamples

buildAppleSamples:
	cd ./sentry-samples/kmp-app-cocoapods/iosApp/iosApp && touch iosApp.xcconfig
	cd ./sentry-samples/kmp-app-spm/iosApp && touch iosApp.xcconfig
	cd ./sentry-samples/kmp-app-mvvm-di/iosApp && touch iosApp.xcconfig
	sudo xcode-select --switch /Applications/Xcode.app && /usr/bin/xcodebuild -version
	cd ./sentry-samples/kmp-app-cocoapods/iosApp; pod install
	xcodebuild -workspace ./sentry-samples/kmp-app-cocoapods/iosApp/iosApp.xcworkspace -scheme iosApp -configuration Debug -sdk iphonesimulator -arch arm64
	xcodebuild -project ./sentry-samples/kmp-app-spm/iosApp.xcodeproj -scheme iosApp -configuration Debug -sdk iphonesimulator -arch arm64
	xcodebuild -project ./sentry-samples/kmp-app-mvvm-di/iosApp.xcodeproj -scheme iosApp -configuration Debug -sdk iphonesimulator -arch arm64

# We stop gradle at the end to make sure the cache folders
# don't contain any lock files and are free to be cached.
stop:
	./gradlew --stop
