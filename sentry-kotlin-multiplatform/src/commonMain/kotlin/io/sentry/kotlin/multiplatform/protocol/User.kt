package io.sentry.kotlin.multiplatform.protocol

data class User(
    override var email: String = "",
    override var id: String = "",
    override var username: String = "",
    override var ipAddress: String? = null,
    override var other: MutableMap<String, String>? = null,
    override var unknown: MutableMap<String, Any>? = null,
) : ISentryUser {

    constructor(user: ISentryUser) : this(
        user.email,
        user.id,
        user.username,
        user.ipAddress,
        user.other,
        user.unknown
    )

    // This secondary constructor allows Swift also to init without specifying nil explicitly
    // example: User.init() instead of User.init(user: nil)
    constructor() : this("", "", "", null, null, null)

    companion object {
        fun fromMap(map: Map<String, String>) = User().apply {
            email = map["email"].orEmpty()
            username = map["username"].orEmpty()
            id = map["id"].orEmpty()
            ipAddress = map["ip_address"]
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
    var other: MutableMap<String, String>?

    /** Unknown fields, only internal usage. */
    var unknown: MutableMap<String, Any>?
}
