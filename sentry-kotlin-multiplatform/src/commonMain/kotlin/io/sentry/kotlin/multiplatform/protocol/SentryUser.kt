package io.sentry.kotlin.multiplatform.protocol

class SentryUser() : ISentryUser {

    override var email: String = ""
    override var id: String = ""
    override var username: String = ""
    override var ipAddress: String? = null
    override var other: MutableMap<String, String> = HashMap()
    override var unknown: MutableMap<String, Any> = HashMap()

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
    var other: MutableMap<String, String>

    /** Unknown fields, only internal usage. */
    var unknown: MutableMap<String, Any>
}
