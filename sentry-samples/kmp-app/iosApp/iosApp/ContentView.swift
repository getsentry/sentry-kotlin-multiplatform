import SwiftUI
import shared

struct ContentView: View {
	var body: some View {
		Text("KMP Sample App " + Platform().platform)
        VStack() {
            Button("Capture Message") {
                SentryKMP().captureMessage(message: "KMP Sample App " + Platform().platform)
            }
            Button("Capture Exception") {
                LoginImpl().login("MyUsername")
            }
            Button("Hard Crash") {
                LoginImpl().login()
            }
        }
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
