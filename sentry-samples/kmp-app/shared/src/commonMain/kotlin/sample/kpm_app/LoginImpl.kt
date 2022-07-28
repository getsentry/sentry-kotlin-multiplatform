package sample.kpm_app

import io.sentry.kotlin.multiplatform.SentryKMP

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
            SentryKMP.captureException(exception)
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
