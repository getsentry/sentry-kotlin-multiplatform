package sample.kpm_app

import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryBreadcrumb
import io.sentry.kotlin.multiplatform.SentryLevel
import io.sentry.kotlin.multiplatform.SentryUser

class InvalidUsernameException(message: String) : Exception(message)

object LoginImpl {
    /**
     * login() throws a either checked InvalidUsernameException
     * or an IllegalArgumentException that crashes the app.
     *
     */
    fun login(username: String? = null) {
        try {
            validateUsername(username)
        } catch (exception: InvalidUsernameException) {
            Sentry.captureException(exception) {
                it.addBreadcrumb(SentryBreadcrumb.debug("this is a test breadcrumb"))
                it.setContext("Login", "Failed with Invalid Username")
                it.setTag("Login", "Failed Authentication")
                it.level = SentryLevel.WARNING
                it.user = SentryUser()
                it.user?.username = "John Doe"
                it.user?.email = "john@doe.com"
            }
        } catch (exception: IllegalArgumentException) {
            throw exception
        }
    }

    private fun validateUsername(username: String?) {
        if (username == null) {
            throw IllegalArgumentException("Username cannot be null")
        }
        throw InvalidUsernameException("Username does not exist")
    }
}
