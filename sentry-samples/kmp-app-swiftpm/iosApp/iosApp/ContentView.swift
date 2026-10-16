import SwiftUI
import SentrySwiftPmSample

struct ContentView: View {
    var body: some View {
        VStack(spacing: 16) {
            Text("Official SwiftPM Sample")
            Button("Capture Message") {
                SentryExampleKt.captureExample()
            }
            Button("Capture Exception") {
                SentryExampleKt.captureExampleException()
            }
        }
    }
}
