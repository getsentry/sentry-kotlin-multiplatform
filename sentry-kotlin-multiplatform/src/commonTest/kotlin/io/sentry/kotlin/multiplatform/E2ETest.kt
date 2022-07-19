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

@Serializable
data class Event(val id: String)

class SentryTests : BaseSentryTest() {
    private val dsn = "https://83f281ded2844eda83a8a413b080dbb9@o447951.ingest.sentry.io/5903800"
    private val retryCount = 10
    private val retryInterval: Long = 15000
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

    suspend fun waitForEventRetrieval(eventId: String): Event {
        var json = ""
        var count = retryCount
        while (json.isEmpty() && count > 0) {
            delay(retryInterval)
            json = fetchEvent(eventId)
            count -= 1
        }
        return Json { ignoreUnknownKeys = true }.decodeFromString(json)
    }

    @BeforeTest
    fun init() {
        SentryKMP.start(context) {
            it.dsn = dsn
        }
    }

    @Test
    fun `capture message and persist it in the sentry project`() {
        runBlocking {
            if (platform != "Apple" && platform != "JVM") {
                val eventId = SentryKMP.captureMessage("Test running on $platform")
                val result = waitForEventRetrieval(eventId.toString())
                assertEquals(result.id, eventId.toString())
            }
        }
    }

    @Test
    fun `capture exception and persist it in the sentry project`() {
        runBlocking {
            if (platform != "Apple" && platform != "JVM") {
                val eventId = SentryKMP.captureException(IllegalArgumentException("Test exception on platform $platform"))
                val result = waitForEventRetrieval(eventId.toString())
                assertEquals(result.id, eventId.toString())
            }
        }
    }

    @AfterTest
    fun close() {
        SentryKMP.close()
    }
}
