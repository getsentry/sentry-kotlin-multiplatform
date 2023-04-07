package io.sentry.kmp.demo.android

import android.app.Application
import android.content.Context
import android.util.Log
import io.sentry.kmp.demo.initKoin
import io.sentry.kmp.demo.sentry.initSentry
import org.koin.dsl.module

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()

        initSentry(this)

        initKoin(
            module {
                single<Context> { this@MainApp }
                single {
                    { Log.i("Startup", "Hello from Android/Kotlin!") }
                }
            }
        )
    }
}
