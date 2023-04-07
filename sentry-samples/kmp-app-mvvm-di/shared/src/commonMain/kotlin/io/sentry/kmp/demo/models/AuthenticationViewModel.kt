package io.sentry.kmp.demo.models

import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryLevel
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb

class LoginException(message: String) : Exception(message)

class AuthenticationViewModel : ViewModel() {

    fun login(withError: Boolean): Boolean {
        return if (withError) {
            try {
                throw LoginException("Error logging in")
            } catch (exception: Exception) {
                Sentry.captureException(exception) {
                    val breadcrumb = Breadcrumb.error("Error during login").apply {
                        setData("touch event", "on login")
                    }
                    it.addBreadcrumb(breadcrumb)
                    it.setContext("Login", "Failed due to ...")
                    it.setTag("login", "failed")
                    it.level = SentryLevel.ERROR
                }
                false
            }
        } else {
            true
        }
    }

    fun signUp() {
        Sentry.crash()
    }
}
