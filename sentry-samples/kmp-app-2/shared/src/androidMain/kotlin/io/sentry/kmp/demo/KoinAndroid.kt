package io.sentry.kmp.demo

import io.sentry.kmp.demo.models.AuthenticationViewModel
import io.sentry.kmp.demo.models.HomeViewModel
import org.koin.dsl.module

actual val platformModule = module {
    single { AuthenticationViewModel() }
    single { HomeViewModel() }
}
