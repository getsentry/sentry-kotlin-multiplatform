package sentry.kmp.demo

import org.koin.dsl.module
import sentry.kmp.demo.models.AuthenticationViewModel
import sentry.kmp.demo.models.HomeViewModel

actual val platformModule = module {
  single { AuthenticationViewModel() }
  single { HomeViewModel() }
}
