package sentry.kmp.demo.models

import io.sentry.kotlin.multiplatform.ScopeCallback
import io.sentry.kotlin.multiplatform.Sentry

class ProfileUpdateException(message: String) : Exception(message)

class HomeViewModel : ViewModel() {

  val homeText =
      "This screen will show you how we can change the scope of each Sentry event via captureException!"

  private val scopeConfig: ScopeCallback = { it.setContext("home", "logged in") }

  fun updateProfileWithErr() {
    Sentry.captureException(ProfileUpdateException("Error updating profile"), scopeConfig)
  }
}
