package sample.kpm_app

import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.SentryLevel
import io.sentry.kotlin.multiplatform.protocol.SentryUser

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
                val breadcrumb = Breadcrumb.debug("this is a test breadcrumb")
                breadcrumb.setData("touch event", "on login")
                it.addBreadcrumb(breadcrumb)
                it.setContext("Login", "Failed with Invalid Username")
                it.setTag("login", "failed auth")
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
