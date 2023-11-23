package sentry.kmp.demo.models

import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryLevel
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb

class LoginException(message: String) : Exception(message)

class AuthenticationViewModel : ViewModel() {

    fun login(withError: Boolean): Boolean {
        return if (withError) {
            val transaction = Sentry.startTransaction("Authentication", "login", bindToScope = true)
            try {
                throw LoginException("Error logging in")
            } catch (exception: Exception) {
                val activeSpan = Sentry.getSpan()
                activeSpan?.startChild("child demo span", "child demo span description")
                Sentry.captureException(exception) {
                    val breadcrumb = Breadcrumb.error("Error during login").apply {
                        setData("touch event", "on login")
                    }
                    it.addBreadcrumb(breadcrumb)
                    it.setContext("Login", "Failed due to ...")
                    it.setTag("login", "failed")
                    it.level = SentryLevel.ERROR
                }
                activeSpan?.finish()
                false
            } finally {
                transaction.finish()
            }
        } else {
            true
        }
    }

    fun signUp() {
        Sentry.crash()
    }
}
