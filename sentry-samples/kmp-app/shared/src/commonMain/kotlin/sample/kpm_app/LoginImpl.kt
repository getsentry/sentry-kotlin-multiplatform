package sample.kpm_app

import io.sentry.kotlin.multiplatform.SentryKMP

class InvalidUsernameException(message: String) : Exception(message)

object LoginImpl {
    /**
     * login() throws a checked InvalidUsernameException.
     * The Sentry SDK should capture a handled exception.
     *
     */
    fun login() {
        val username = "MyUser"
        try {
            validateUsername(username)
        } catch (exception: Exception) {
            SentryKMP.captureException(exception)
        }
    }

    /**
     * loginWithIllegalArguments() throws an IllegalArgumentException and crashes the app.
     * The Sentry SDK should capture an unhandled exception.
     *
     */
    fun loginWithIllegalArguments() {
        validateUsername(null)
    }

    private fun validateUsername(username: String?) {
        if (username == null) {
            throw IllegalArgumentException("Username cannot be null")
        }
        throw InvalidUsernameException("Username does not exist")
    }
}