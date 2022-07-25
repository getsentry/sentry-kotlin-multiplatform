import SwiftUI
import shared

@main
struct iOSApp: App {
    let sentry = SentryKMP()

    init() {
        sentry.start { options in
            options.dsn = "https://83f281ded2844eda83a8a413b080dbb9@o447951.ingest.sentry.io/5903800"
        }
    }

	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
