import SwiftUI
import SentrySwiftPmSample

@main
struct iOSApp: App {
    init() {
        SentryExampleKt.initializeSentry()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
