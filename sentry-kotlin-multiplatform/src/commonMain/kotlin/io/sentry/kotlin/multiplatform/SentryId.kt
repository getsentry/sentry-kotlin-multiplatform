package io.sentry.kotlin.multiplatform

class SentryId(private val sentryIdString: String) {
    var uuidString: String = ""
        private set

    companion object {
        val EMPTY_ID: SentryId = SentryId("00000000-0000-0000-0000-000000000000")
    }

    init {
        if (sentryIdString.isEmpty()) {
            uuidString = EMPTY_ID.uuidString
        } else {
            fromStringSentryId(sentryIdString)
        }
    }

    private fun fromStringSentryId(sentryIdString: String) {
        var result = sentryIdString

        if (result.length == 32) {
            // expected format, SentryId is a UUID without dashes
            result = StringBuilder(sentryIdString)
                .insert(8, "-")
                .insert(13, "-")
                .insert(18, "-")
                .insert(23, "-")
                .toString()
        }
        if (result.length != 36) {
            throw IllegalArgumentException(
                "String representation of SentryId has either 32 (UUID no dashes) "
                        + "or 36 characters long (completed UUID). Received: "
                        + sentryIdString
            )
        }
        this.uuidString = result
    }

    override fun toString(): String {
        return uuidString.replace("-", "")
    }
}
