.PHONY: all clean compile dryRelease stop

all: stop clean compile

# deep clean
clean:
	./gradlew clean
	rm -rf distributions

# local deploy
dryRelease:
	./gradlew publishToMavenLocal --no-daemon --no-parallel

# Spotless check's code
checkFormat:
	./gradlew spotlessJavaCheck spotlessKotlinCheck

# Spotless format's code
format:
	./gradlew spotlessApply

# build and run tests
compile:
	./gradlew build
	sudo xcode-select --switch /Applications/Xcode_12.5.1.app && /usr/bin/xcodebuild -version
	cd ./sentry-samples/kmp-app/iosApp; pod install
	xcodebuild -workspace ./sentry-samples/kmp-app/iosApp/iosApp.xcworkspace -scheme iosApp -configuration Debug -sdk iphonesimulator -arch arm64

# We stop gradle at the end to make sure the cache folders
# don't contain any lock files and are free to be cached.
stop:
	./gradlew --stop
