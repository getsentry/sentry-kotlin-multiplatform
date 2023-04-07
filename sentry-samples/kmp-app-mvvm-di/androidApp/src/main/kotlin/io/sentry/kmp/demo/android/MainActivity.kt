package io.sentry.kmp.demo.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.sentry.kmp.demo.android.theme.Theme
import io.sentry.kmp.demo.android.ui.MyApp
import io.sentry.kmp.demo.models.AuthenticationViewModel
import io.sentry.kmp.demo.models.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent

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
