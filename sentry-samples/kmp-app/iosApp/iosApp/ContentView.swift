import SwiftUI
import shared

struct ContentView: View {
	var body: some View {
		Text("KPM Sample App " + Platform().platform)
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}