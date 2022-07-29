package io.sentry.kotlin.multiplatform

class SentryUser(user: ISentryUser) : ISentryUser {
    override var email: String = ""
    override var id: String = ""
    override var username: String = ""
    override var ipAddress: String = ""
    override var other: Map<String, String> = HashMap()
    override var unknown: Map<String, Any> = HashMap()

    init {
        this.email = user.email
        this.id = user.id
        this.username = user.username
        this.ipAddress = user.ipAddress
        this.other = user.other
        this.unknown = user.unknown
    }
}

interface ISentryUser {
    var email: String
    var id: String
    var username: String
    var ipAddress: String
    var other: Map<String, String>
    var unknown: Map<String, Any>
}
