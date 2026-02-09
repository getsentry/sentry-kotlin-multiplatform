package sample.kmp.app

import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryLevel
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.User
import io.sentry.kotlin.multiplatform.protocol.UserFeedback

class InvalidUsernameException(message: String) : Exception(message)

object LoginImpl {
    /**
     * login() throws a either checked InvalidUsernameException
     * or an IllegalArgumentException that crashes the app.
     *
     */
    fun login(username: String? = null) {
        // Message with varargs AND inline attributes
        Sentry.logger.info("Login attempt for user: %s", username) {
            this["source"] = "login-form"
            this["platform"] = Platform().platform
        }

        try {
            validateUsername(username)
        } catch (exception: InvalidUsernameException) {
            val sentryId = Sentry.captureException(exception) {
                val breadcrumb = Breadcrumb.debug("this is a test breadcrumb")
                breadcrumb.setData("touch event", "on login")
                it.addBreadcrumb(breadcrumb)
                it.setContext("Login", "Failed with Invalid Username")
                it.setTag("login", "failed auth")
                it.level = SentryLevel.WARNING
                val user = User().apply {
                    this.username = "John Doe"
                    this.email = "john@doe.com"
                }
                it.user = user
            }
            val userFeedback = UserFeedback(sentryId).apply {
                name = "John Doe"
                email = "john@doe.com"
                comments = "I had an error during login on ${Platform().platform}"
            }
            Sentry.captureUserFeedback(userFeedback)
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
