// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.init
import sample.kpm_app.LoginImpl
import sample.kpm_app.Platform
import sample.kpm_app.configureSharedScope
import sample.kpm_app.optionsConfiguration

@Composable
@Preview
fun App() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val btnBackgroundColor = Color(56, 31, 67)
        Button({
            Sentry.captureMessage("From KMP Sample App: " + Platform().platform)
        }, colors = ButtonDefaults.buttonColors(backgroundColor = btnBackgroundColor)) {
            Text("Capture Message", color = Color.White)
        }
        Button({
            LoginImpl.login("MyUsername")
        }, colors = ButtonDefaults.buttonColors(backgroundColor = btnBackgroundColor)) {
            Text("Capture Exception", color = Color.White)
        }
        Button({
            LoginImpl.login()
        }, colors = ButtonDefaults.buttonColors(backgroundColor = btnBackgroundColor)) {
            Text("Crash", color = Color.White)
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        Sentry.init(optionsConfiguration())

        // Shared scope across all platforms
        configureSharedScope()

        // Add platform specific scope in addition to the shared scope
        Sentry.configureScope {
            it.setContext("JVM Desktop Context", mapOf("context1" to 12, "context2" to false))
            it.addBreadcrumb(Breadcrumb.debug("initialized Sentry on JVM Desktop"))
        }

        App()
    }
}
