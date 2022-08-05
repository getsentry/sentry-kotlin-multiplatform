package com.example.common

import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.User

@Composable
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    sentryInit()

    Sentry.configureScope {
        it.setContext("my value", "test")
        it.user = User().apply {
            username = "John Doe"
            email = "john@doe.com"
        }
    }

    Button(onClick = {
        val breadcrumb = Breadcrumb().apply {
            setMessage("this is a test breadcrumb")
            setData("my", "breadcrumb")
        }
        Sentry.captureMessage("From Compose: ${getPlatformName()}") {
            it.addBreadcrumb(breadcrumb)
        }
    }) {
        Text(text)
    }
}
