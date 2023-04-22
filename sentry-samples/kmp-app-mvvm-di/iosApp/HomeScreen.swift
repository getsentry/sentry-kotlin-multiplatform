import SwiftUI
import shared

struct HomeScreen: View {
    @State private var presentLoginScreen = true
    @State private var showDialog = false
    @State private var dialogMessage = ""

    @Environment(\.presentationMode) var presentationMode
    private var viewModel = KotlinDependencies.shared.getHomeViewModel()

    var body: some View {
        VStack(alignment: .center, spacing: 16) {
            Text("Welcome!")
                .font(.title)
                .fontWeight(.bold)
                .padding(.bottom, 16)
            Text(viewModel.homeText)
                .font(.body)
                .fontWeight(.regular)
                .padding(.bottom, 16)
            Button(action: {
                viewModel.updateProfileWithErr()
                showDialog = true
                dialogMessage = "An error occurred during profile update"
            }) {
                Text("Update Profile (error)")
            }
            Button(action: { presentLoginScreen = true }) {
                Text("Log Out")
            }
            .sheet(isPresented: $presentLoginScreen) {
                LoginScreen()
            }
            .frame(maxWidth: .infinity)
            .padding(.top, 16)
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
