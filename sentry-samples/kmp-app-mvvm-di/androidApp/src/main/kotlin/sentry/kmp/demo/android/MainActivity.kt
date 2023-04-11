package sentry.kmp.demo.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import sentry.kmp.demo.android.theme.Theme
import sentry.kmp.demo.android.ui.MyApp
import sentry.kmp.demo.models.AuthenticationViewModel
import sentry.kmp.demo.models.HomeViewModel

class MainActivity : ComponentActivity(), KoinComponent {

    private val authenticationViewModel: AuthenticationViewModel by viewModel()
    private val homeViewModel: HomeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Theme {
                MyApp(authenticationViewModel, homeViewModel)
            }
        }
    }
}
