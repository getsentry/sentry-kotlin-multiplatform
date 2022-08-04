package io.sentry.kotlin.multiplatform.protocol

data class User(
    override var email: String = "",
    override var id: String = "",
    override var username: String = "",
    override var ipAddress: String? = null,
    override var other: MutableMap<String, String> = mutableMapOf(),
    override var unknown: MutableMap<String, Any> = mutableMapOf(),
) : ISentryUser {

    constructor(user: ISentryUser? = null) : this("", "", "", null, mutableMapOf(), mutableMapOf()) {
        user?.let {
            this.email = it.email
            this.id = it.id
            this.username = it.username
            this.ipAddress = it.ipAddress
            this.other = it.other
            this.unknown = it.unknown
        }
    }

    // This secondary constructor allows Swift also to init without specifying nil explicitly
    // example: User.init() instead of User.init(user: nil)
    constructor() : this("", "", "", null, mutableMapOf(), mutableMapOf())

    companion object {
        fun fromMap(map: Map<String, String>): User {
            val user = User()
            user.email = map["email"].orEmpty()
            user.username = map["username"].orEmpty()
            user.id = map["id"].orEmpty()
            user.ipAddress = map["ip_address"]
            return user
        }
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
