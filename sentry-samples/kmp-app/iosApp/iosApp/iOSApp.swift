import SwiftUI
import shared

@main
struct iOSApp: App {
    let sentry = Sentry.shared
    
    init() {
        // Initialize Sentry using shared code
        AppSetupKt.initializeSentry()

        // Shared scope across all platforms
        AppSetupKt.configureSentryScope()

        // Automatically capture http client error
        captureHttpClientError()
        
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

	func captureHttpClientError() {
        let url = URL(string: "https://httpbin.org/status/404")!
        var request = URLRequest(url: url)
        let task = URLSession.shared.dataTask(with: url) { data, response, error in
            guard let response = response as? HTTPURLResponse else {
                print("Invalid response received")
                return
            }

            if response.statusCode >= 200 && response.statusCode < 300 {
                // handle successful response
                if let data = data {
                    let image = UIImage(data: data)
                    print("image: \(image)")
                }
            } else {
                // handle error response
                print("HTTP Request Failed with status code: \(response.statusCode)")
            }
        }

        task.resume()
	}
}
