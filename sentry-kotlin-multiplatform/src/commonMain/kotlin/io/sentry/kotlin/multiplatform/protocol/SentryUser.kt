package io.sentry.kotlin.multiplatform.protocol

import kotlin.properties.Delegates

class SentryUser() : ISentryUser {

    private fun <T> onFieldChangeDelegate(init: T) = Delegates.observable(init) { _, _, _ ->
        onFieldChanged?.let { it() }
    }

    override var email: String by onFieldChangeDelegate("")
    override var id: String by onFieldChangeDelegate("")
    override var username: String by onFieldChangeDelegate("")
    override var ipAddress: String? by onFieldChangeDelegate("")
    override var other: Map<String, String> by onFieldChangeDelegate(HashMap())
    override var unknown: Map<String, Any> by onFieldChangeDelegate(HashMap())

    var onFieldChanged: (() -> Unit)? = null

    constructor(user: ISentryUser) : this() {
        this.email = user.email
        this.id = user.id
        this.username = user.username
        this.ipAddress = user.ipAddress
        this.other = user.other
        this.unknown = user.unknown
    }

    override fun equals(other: Any?): Boolean {
        if (other is SentryUser) {
            return this.email == other.email
                    && this.id == other.id
                    && this.username == other.username
                    && this.ipAddress == other.ipAddress
                    && this.unknown == other.unknown
                    && this.other == other.other
        }
        return false
    }
}

interface ISentryUser {

    /** The user's email */
    var email: String

    /** The user's id */
    var id: String

    /** The user's username */
    var username: String

    /** The user's ip address*/
    var ipAddress: String?

    /**
     * Additional arbitrary fields, as stored in the database (and sometimes as sent by clients). All
     * data from `self.other` should end up here after store normalization.
     */
    var other: Map<String, String>

    /** Unknown fields, only internal usage. */
    var unknown: Map<String, Any>
}
