package com.hereliesaz.julesapisdk

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class JulesHttpClientTest {

    // Define the permissive Json object, just like in your other tests
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Test
    fun `get method handles success`() = runBlocking {
        var capturedUrl = ""
        var capturedApiKey = ""

        val mockEngine = MockEngine { request ->
            capturedUrl = request.url.toString()
            capturedApiKey = request.headers["X-Goog-Api-Key"] ?: ""
            respond(
                content = """{"name":"test", "id": "123", "createTime": "now", "updateTime": "now", "url": "url", "type": "type"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) { 
                json(json) // Use the permissive json object, not json()
            }
        }
        val julesHttpClient = JulesHttpClient(apiKey = "test-api-key", httpClient = httpClient)

        val source = julesHttpClient.get<Source>("/test")

        assertEquals("https://jules.googleapis.com/v1alpha/test", capturedUrl)
        assertEquals("test-api-key", capturedApiKey)
        assertEquals("test", source.name)
    }

    @Test
    fun `get method throws JulesApiException on non-2xx response`() {
        val mockEngine = MockEngine {
            respond(
                content = """{"error":"not found"}""",
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) { 
                json(json) // Use the permissive json object
            }
        }
        val julesHttpClient = JulesHttpClient(apiKey = "test-api-key", httpClient = httpClient)

        val exception = assertThrows<JulesApiException> {
            runBlocking {
                julesHttpClient.get<Source>("/test")
            }
        }
        assertEquals(404, exception.statusCode)
        assertEquals("""{"error":"not found"}""", exception.responseBody)
    }

    @Test
    fun `post method with body handles success`() = runBlocking {
        var capturedUrl = ""
        var capturedApiKey = ""
        var capturedBody: String? = null

        val mockEngine = MockEngine { request ->
            capturedUrl = request.url.toString()
            capturedApiKey = request.headers["X-Goog-Api-Key"] ?: ""
            capturedBody = request.body.toString()
            respond(
                content = """{"name":"test", "id": "123", "createTime": "now", "updateTime": "now", "url": "url", "type": "type"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) { 
                json(json) // Use the permissive json object
            }
        }
        val julesHttpClient = JulesHttpClient(apiKey = "test-api-key", httpClient = httpClient)

        val source = julesHttpClient.post<Source>("/test", "body")

        assertEquals("https://jules.googleapis.com/v1alpha/test", capturedUrl)
        assertEquals("test-api-key", capturedApiKey)
        assertNotNull(capturedBody)
        assertEquals("test", source.name)
    }

    @Test
    fun `post method without body handles success`() = runBlocking {
        var capturedUrl = ""
        var capturedApiKey = ""

        val mockEngine = MockEngine { request ->
            capturedUrl = request.url.toString()
            capturedApiKey = request.headers["X-Goog-Api-Key"] ?: ""
            respond(
                content = """{}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) { 
                json(json) // Use the permissive json object
            }
        }
        val julesHttpClient = JulesHttpClient(apiKey = "test-api-key", httpClient = httpClient)

        julesHttpClient.post<Unit>("/test")

        assertEquals("https://jules.googleapis.com/v1alpha/test", capturedUrl)
        assertEquals("test-api-key", capturedApiKey)
    }
}
