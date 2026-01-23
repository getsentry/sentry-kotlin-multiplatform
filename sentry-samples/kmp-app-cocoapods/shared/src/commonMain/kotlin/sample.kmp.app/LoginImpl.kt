package sample.kmp.app

import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryLevel
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.User
import io.sentry.kotlin.multiplatform.protocol.UserFeedback
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class InvalidUsernameException(message: String) : Exception(message)

object LoginImpl {
    /**
     * login() throws either a checked InvalidUsernameException
     * or an IllegalArgumentException that crashes the app.
     */
    @OptIn(ExperimentalUuidApi::class)
    fun login(username: String? = null) {
        Sentry.logger.info {
            message("User tries to login with username: %s and id %s", username, Uuid.random().toString())
            attributes {
                this["test-attribute"] = "test-value"
            }
        }
        mutableMapOf<String, String>(
            "another-attribute" to "another-value"
        )
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
