import SwiftUI
import shared

struct LoginScreen: View {
    @State private var email = "user@sentrydemo.com"
    @State private var password = "randompassword"
    @State private var enableLoginError = true
    @State private var showDialog = false
    @State private var dialogMessage = ""
    @State private var isLoggedIn = false
    @Environment(\.presentationMode) var presentationMode

    private var viewModel = KotlinDependencies.shared.getAuthenticationViewModel()
    
    var body: some View {
        VStack(alignment: .center, spacing: 16) {
            Text("Sentry Demo")
                .font(.title)
                .fontWeight(.bold)
                .padding(.bottom, 16)
            TextField("Email", text: $email)
                .keyboardType(.emailAddress)
                .textFieldStyle(RoundedBorderTextFieldStyle())
            SecureField("Password", text: $password)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .padding(.top, 16)
            Button(action: {
                let succeded = viewModel.login(withError: enableLoginError)
                if succeded {
                    // present home screen
                    presentationMode.wrappedValue.dismiss()
                } else {
                    dialogMessage = "An error occurred during login"
                    showDialog = true
                }
            }) {
                if (enableLoginError) {
                    Text("Log In (error)")
                } else {
                    Text("Log In")
                }
            }
            .frame(maxWidth: .infinity)
            .padding(.top, 16)
            Button(action: {
                viewModel.signUp()
            }) {
                Text("Sign up (crash)")
            }
            .frame(maxWidth: .infinity)
            .padding(.top, 16)
            HStack {
                Text("Enable login error")
                Toggle("", isOn: $enableLoginError)
                    .padding(.trailing, 8)
            }
        }
        .padding(16)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .alert(isPresented: $showDialog) {
            Alert(
                title: Text("Error"),
                message: Text(dialogMessage),
                dismissButton: .default(Text("OK")) {
                    showDialog = false
                }
            )
        }
    }
}
