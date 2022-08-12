import SwiftUI
import shared

@main
struct iOSApp: App {
    let sentry = Sentry()

    init() {
        sentry.start { options in
            options.dsn = "https://83f281ded2844eda83a8a413b080dbb9@o447951.ingest.sentry.io/5903800"
        }
        
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
            if let path = Bundle.main.path(forResource: "sentry", ofType: "png") {
                let imageAttachment = Attachment(pathname: path, filename: "sentry.png")
                scope.addAttachment(attachment: imageAttachment)
            }
        }
    }

	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
