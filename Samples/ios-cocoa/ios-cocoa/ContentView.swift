import sentry_kotlin_multiplatform
import SwiftUI

struct ContentView: View {
    
    var addBreadcrumbAction: () -> Void = {

    }
    
    var captureMessageAction: () -> Void = {
        let sentry = Sentry()
        sentry.captureMessage(msg: "Yeah captured a message")
    }

    var captureExceptionAction: () -> Void = {
        let sentry = Sentry()
        let throwable = KotlinThrowable(message: "Kotlin Throwable")
        sentry.captureException(throwable: throwable)
    }
    
    var body: some View {
        VStack {
            Button(action: addBreadcrumbAction) {
                Text("Add Breadcrumb")
            }
            
            Button(action: captureMessageAction) {
                Text("Capture Message")
            }
            
            Button(action: captureExceptionAction) {
                Text("Capture Exception")
            }
            
            Button(action: {
                let sentry = Sentry()
                sentry.crash()
            }) {
                Text("Crash")
            }
            
        }
        
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
