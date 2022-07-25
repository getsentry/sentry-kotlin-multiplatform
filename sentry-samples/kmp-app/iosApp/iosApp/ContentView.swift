import SwiftUI
import shared

struct ContentView: View {
	var body: some View {
		Text("KPM Sample App " + Platform().platform)
        VStack() {
            Button("Capture Message") {
                SentryKMP().captureMessage(message: "KMP Sample App " + Platform().platform)
            }
            Button("Capture Exception") {
                SharedBusinessLogic().doException()
            }
            Button("Hard Crash") {
                SharedBusinessLogic().hardCrash()
            }
        }
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}