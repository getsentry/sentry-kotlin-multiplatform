package io.sentry.kotlin.multiplatform.protocol

/** Information about the user who triggered an event. */
public data class User(
    /** The user's email */
    var email: String? = null,

    /** The user's id */
    var id: String? = null,

    /** The user's username */
    var username: String? = null,

    /** The user's ip address*/
    var ipAddress: String? = null,

    /**
     * Additional arbitrary fields, as stored in the database (and sometimes as sent by clients). All
     * data from `self.other` should end up here after store normalization.
     */
    var other: MutableMap<String, String>? = null,

    /** Unknown fields, only internal usage. */
    var unknown: MutableMap<String, Any>? = null
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
    public constructor() : this(null, null, null, null, null, null)
}
