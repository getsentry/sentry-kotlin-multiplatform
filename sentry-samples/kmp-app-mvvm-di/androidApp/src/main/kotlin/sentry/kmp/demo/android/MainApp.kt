package sentry.kmp.demo.android

import android.app.Application
import android.content.Context
import android.util.Log
import org.koin.dsl.module
import sentry.kmp.demo.initKoin
import sentry.kmp.demo.sentry.initSentry

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
