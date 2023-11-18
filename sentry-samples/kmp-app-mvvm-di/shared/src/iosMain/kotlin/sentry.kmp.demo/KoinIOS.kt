package sentry.kmp.demo

import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.dsl.module
import sentry.kmp.demo.models.AuthenticationViewModel
import sentry.kmp.demo.models.HomeViewModel

fun initKoinIos(): KoinApplication = initKoin(module {})

actual val platformModule = module {
  single { AuthenticationViewModel() }
  single { HomeViewModel() }
}

@Suppress("unused") // Called from Swift
object KotlinDependencies : KoinComponent {
  fun getAuthenticationViewModel() = getKoin().get<AuthenticationViewModel>()

  fun getHomeViewModel() = getKoin().get<HomeViewModel>()
}
