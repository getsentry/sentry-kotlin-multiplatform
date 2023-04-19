import SwiftUI
import shared

struct ContentView: View {
	var body: some View {
		Text("KMP Sample App " + Platform().platform)
        VStack() {
            Button("Capture Message") {
                Sentry().captureMessage(message: "From KMP Sample App " + Platform().platform)
            }
            Button("Capture Exception") {
                LoginImpl().login(username: "MyUsername")
            }
            Button("Capture Http Client Error") {
                HttpClientKt.captureHttpClientError()
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
