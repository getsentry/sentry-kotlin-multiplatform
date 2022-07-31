package io.sentry.kotlin.multiplatform

class SentryUser() : ISentryUser {
    override var email: String = ""
    override var id: String = ""
    override var username: String = ""
    override var ipAddress: String = ""
    override var other: Map<String, String> = HashMap()
    override var unknown: Map<String, Any> = HashMap()

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
    var email: String
    var id: String
    var username: String
    var ipAddress: String
    var other: Map<String, String>
    var unknown: Map<String, Any>
}
