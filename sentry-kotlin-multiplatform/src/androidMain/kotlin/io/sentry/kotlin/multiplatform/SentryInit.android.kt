package io.sentry.kotlin.multiplatform

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import io.sentry.android.core.SentryAndroid
import io.sentry.kotlin.multiplatform.extensions.setSdkVersionAndName
import io.sentry.kotlin.multiplatform.extensions.toAndroidSentryOptionsCallback

internal actual fun initSentry(configuration: OptionsConfiguration) {
    val options = SentryOptions()
    configuration.invoke(options)

    val context = applicationContext ?: run {
        // TODO: add logging later
        return
    }

    SentryAndroid.init(context) { sentryOptions ->
        options.toAndroidSentryOptionsCallback().invoke(sentryOptions)
    }
}

internal actual fun initSentryWithPlatformOptions(configuration: PlatformOptionsConfiguration) {
    val context = applicationContext ?: run {
        // TODO: add logging later
        return
    }

    SentryAndroid.init(context) { options ->
        configuration(options)
        options.prepareForInit()
    }
}

internal var applicationContext: Context? = null
    private set

public actual typealias Context = Context

/**
 * A ContentProvider that does NOT store or provide any data for read or write operations.
 *
 * It's only purpose is to retrieve and store the application context in an internal top-level
 * variable [applicationContext]. The context is used for [SentryAndroid.init].
 *
 * This does not allow for overriding the abstract query, insert, update, and delete operations
 * of the [ContentProvider].
 */
internal class SentryContextProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        val context = context
        if (context != null) {
            applicationContext = context.applicationContext
        } else {
            error("Context cannot be null")
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        error("Not allowed.")
    }

    override fun getType(uri: Uri): String? {
        error("Not allowed.")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        error("Not allowed.")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        error("Not allowed.")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        error("Not allowed.")
    }
}
