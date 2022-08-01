import SwiftUI
import shared

struct ContentView: View {
	var body: some View {
		Text("KMP Sample App " + Platform().platform)
        VStack() {
            Button("Capture Message") {
                Sentry().captureMessage(message: "KMP Sample App " + Platform().platform) { scope in
                    scope.user = SentryUser()
                    scope.user?.username = "John Message"
                    scope.user?.email = "john@message.com"
                    scope.setTag(key: "test-tag-key", value: "test-tag-value")
                }
            }
            Button("Capture Exception") {
                LoginImpl().login(username: "MyUsername")
            }
            Button("Hard Crash") {
                LoginImpl().login(username: nil)
            }
        }
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
