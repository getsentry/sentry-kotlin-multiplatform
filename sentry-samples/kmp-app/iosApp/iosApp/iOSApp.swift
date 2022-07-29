import SwiftUI
import shared

@main
struct iOSApp: App {
    let sentry = SentryKMP()

    init() {
        sentry.start { options in
            options.dsn = "https://83f281ded2844eda83a8a413b080dbb9@o447951.ingest.sentry.io/5903800"
        }
        sentry.configureScope { scope in
            scope.setContext(key: "hello", value: 12)
            scope.setLevel(level: SentryLevel.fatal)
        }
    }

	var body: some Scene {
		WindowGroup {
			ContentView()
            VStack(alignment: .leading) {
                Button("Show details") {
                    sentry.captureMessage(message: "KMP Sample App " + Platform().platform)
                    SharedBusinessLogic().doException()
                }
            }
		}
	}
}
