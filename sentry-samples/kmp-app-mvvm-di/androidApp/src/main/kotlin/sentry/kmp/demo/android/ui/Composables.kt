package sentry.kmp.demo.android.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import sentry.kmp.demo.models.AuthenticationViewModel
import sentry.kmp.demo.models.HomeViewModel

@Composable
fun MyApp(authenticationViewModel: AuthenticationViewModel, homeViewModel: HomeViewModel) {
  val navController = rememberNavController()

  NavHost(navController = navController, startDestination = "login") {
    composable("login") { LoginScreen(navController, authenticationViewModel) }
    composable("home") { HomeScreen(navController, homeViewModel) }
  }
}
