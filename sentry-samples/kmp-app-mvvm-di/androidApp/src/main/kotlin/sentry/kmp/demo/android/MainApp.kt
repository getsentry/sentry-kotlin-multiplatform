package sentry.kmp.demo.android

import android.app.Application
import android.content.Context
import org.koin.dsl.module
import sentry.kmp.demo.initKoin
import sentry.kmp.demo.sentry.initializeSentry

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()

        initializeSentry()

        initKoin(
            module {
                single<Context> { this@MainApp }
            }
        )
    }
}
