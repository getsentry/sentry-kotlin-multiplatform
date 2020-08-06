import SwiftUI
import app

struct ContentView: View {
    var body: some View {
        Text(Proxy().proxyHello())
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
