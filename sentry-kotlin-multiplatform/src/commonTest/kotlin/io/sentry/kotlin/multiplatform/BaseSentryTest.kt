package io.sentry.kotlin.multiplatform

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

private const val dsn = "https://83f281ded2844eda83a8a413b080dbb9@o447951.ingest.sentry.io/5903800"

@Serializable
data class Event(val id: String)

class SentryTests : BaseSentryTest() {

    private val client = HttpClient()

    suspend fun fetchEvent(eventId: String): String {
        val url = "https://sentry.io/api/0/projects/sentry-sdks/sentry-kotlin-multiplatform/events/${eventId}/"
        val response = client.get(url) {
            headers {
                append(
                    HttpHeaders.Authorization,
                    "Bearer ${authToken()}"
                )
            }
        }
        return response.bodyAsText()
    }

    @BeforeTest
    fun init() {
        SentryKMP.start(context) {
            it.dsn = dsn
        }
    }

    @Test
    fun captureMessage() {
        runBlocking {
            if (platform != "Apple" && platform != "JVM") {
                val eventId = SentryKMP.captureMessage("Test running on $platform")

                // Delay makes sure that the event has already persisted on sentry.io
                delay(15000)

                val json = fetchEvent(eventId.toString())
                val result = Json { ignoreUnknownKeys = true }.decodeFromString<Event>(json)
                assertEquals(result.id, eventId.toString())
            }
        }
    }

    @Test
    fun captureException() {
        runBlocking {
            if (platform != "Apple" && platform != "JVM") {
                val eventId = SentryKMP.captureException(IllegalArgumentException("Test exception on platform $platform"))

                // Delay makes sure that the event has already persisted on sentry.io
                delay(15000)

                val json = fetchEvent(eventId.toString())
                val result = Json { ignoreUnknownKeys = true }.decodeFromString<Event>(json)

                assertEquals(result.id, eventId.toString())
            }
        }
    }

    @AfterTest
    fun close() {
        SentryKMP.close()
    }
}

expect abstract class BaseSentryTest() {
    val context: Any?
    val platform: String
    fun authToken(): String
}
