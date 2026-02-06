package sample.kmp.app

import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryAttributes
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
        // Variant A: Simple message with varargs
        Sentry.logger.info("User tries to login with username: %s and id %s", username, Uuid.random().toString())

        // Variant B: Message with inline attributes lambda
        Sentry.logger.info("User login attempt") {
            this["username"] = username ?: "null"
            this["login-id"] = Uuid.random().toString()
            this["attempt-count"] = 1
            this["is-retry"] = false
            this["latency-ms"] = 23.5
        }

        // Variant C: Full DSL builder with message() and attributes()
        Sentry.logger.info {
            message("User tries to login with username: %s and id %s", username, Uuid.random().toString())
            attributes {
                this["test-attribute"] = "test-value"
            }
        }

        // Variant C: DSL builder with plain message (no template)
        Sentry.logger.warn {
            message("Login validation starting")
        }

        // Variant C: DSL builder with prebuilt SentryAttributes
        val prebuiltAttrs = SentryAttributes.of(
            "source" to "login-form",
            "version" to 2
        )
        Sentry.logger.debug {
            message("Prebuilt attributes test for user: %s", username)
            attributes(prebuiltAttrs)
        }

        // Variant C: DSL builder with multiple attributes blocks (they merge)
        Sentry.logger.trace {
            message("Detailed trace log")
            attributes {
                this["step"] = "pre-validation"
            }
            attributes {
                this["component"] = "auth"
            }
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
