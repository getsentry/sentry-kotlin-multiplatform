import SwiftUI
import app

struct ContentView: View {
    var body: some View {
        Text("\(Proxy.init().proxyHello())")
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
