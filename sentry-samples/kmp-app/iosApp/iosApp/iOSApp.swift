import SwiftUI
import shared

@main
struct iOSApp: App {
    let sentry = Sentry()

    init() {
        // Initialize Sentry using shared code
        AppSetupKt.start()
        
        // Shared scope across all platforms
        AppSetupKt.configureSharedScope()

        // Add platform specific scope in addition to the shared scope
        sentry.configureScope { scope in
            scope.setContext(key: "iOS Context", value: [
                "context1": 20,
                "context2": true
            ])
            let breadcrumb = Breadcrumb.companion.debug(message: "initialized Sentry on iOS")
            scope.addBreadcrumb(breadcrumb: breadcrumb)
        }
    }

	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
