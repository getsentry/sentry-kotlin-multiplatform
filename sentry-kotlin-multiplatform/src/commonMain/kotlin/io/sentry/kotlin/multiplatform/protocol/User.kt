package io.sentry.kotlin.multiplatform.protocol

data class User(val user: ISentryUser? = null) : ISentryUser {

    override var email: String = ""
    override var id: String = ""
    override var username: String = ""
    override var ipAddress: String? = null
    override var other: MutableMap<String, String> = HashMap()
    override var unknown: MutableMap<String, Any> = HashMap()

    // This secondary constructor allows Swift also to init without specifying nil explicitly
    // example: User.init() instead of User.init(user: nil)
    constructor() : this(null)

    init {
        this.email = user?.email.toString()
        this.id = user?.id.toString()
        this.username = user?.username.toString()
        // if ipAddress is invalid ("" is invalid), the sentry.io project will show an invalid value error
        this.ipAddress = user?.ipAddress
        user?.other?.let { this.other = it }
        user?.unknown?.let { this.unknown = it }
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
