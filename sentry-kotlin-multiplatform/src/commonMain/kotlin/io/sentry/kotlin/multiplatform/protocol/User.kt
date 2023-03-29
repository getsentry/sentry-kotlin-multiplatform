package io.sentry.kotlin.multiplatform.protocol

public data class User(
    /** The user's email */
    public var email: String = "",

    /** The user's id */
    public var id: String = "",

    /** The user's username */
    public var username: String = "",

    /** The user's ip address*/
    public var ipAddress: String? = null,

    /**
     * Additional arbitrary fields, as stored in the database (and sometimes as sent by clients). All
     * data from `self.other` should end up here after store normalization.
     */
    public var other: MutableMap<String, String>? = null,

    /** Unknown fields, only internal usage. */
    public var unknown: MutableMap<String, Any>? = null
) {

    public constructor(user: User) : this(
        user.email,
        user.id,
        user.username,
        user.ipAddress,
        user.other,
        user.unknown
    )

    // This secondary constructor allows Swift also to init without specifying nil explicitly
    // example: User.init() instead of User.init(user: nil)
    public constructor() : this("", "", "", null, null, null)
}
