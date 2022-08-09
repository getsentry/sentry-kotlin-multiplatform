package io.sentry.kotlin.multiplatform

import kotlin.jvm.JvmInline

expect class Attachment : IAttachment {

    override val filename: String

    override val bytes: ByteArray?

    override val contentType: String?

    override val pathname: String?

    constructor(bytes: ByteArray, filename: String)

    constructor(bytes: ByteArray, filename: String, contentType: String?)

    constructor(pathname: String)

    constructor(pathname: String, filename: String)

    constructor(pathname: String, filename: String, contentType: String?)
}

interface IAttachment {
    val bytes: ByteArray?

    val contentType: String?

    val pathname: String?

    val filename: String
}
