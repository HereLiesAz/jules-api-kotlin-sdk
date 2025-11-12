package com.hereliesaz.julesapisdk

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class JulesHttpClientTest {
    private fun createMockClient(mockEngine: MockEngine): JulesHttpClient {
        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
        }
        return JulesHttpClient("test-key", "https://test.com", "v1", httpClient = httpClient)
    }

    @Test
    fun `get method handles success`() = runBlocking {
        val mockEngine = MockEngine {
            respond(
                content = """{"key":"value"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val client = createMockClient(mockEngine)
        val response = client.get<Map<String, String>>("/test")
        assertTrue(response is SdkResult.Success)
        assertEquals(mapOf("key" to "value"), (response as SdkResult.Success).data)
    }

    @Test
    fun `get method handles failure`() = runBlocking {
        val mockEngine = MockEngine {
            respond(
                content = """{"error":"not found"}""",
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val client = createMockClient(mockEngine)
        val response = client.get<Map<String, String>>("/test")
        assertTrue(response is SdkResult.Error)
        assertEquals(404, (response as SdkResult.Error).code)
        assertEquals("""{"error":"not found"}""", response.body)
    }

    @Test
    fun `post method with body handles success`() = runBlocking {
        val mockEngine = MockEngine {
            respond(
                content = """{"status":"created"}""",
                status = HttpStatusCode.Created,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val client = createMockClient(mockEngine)
        val response = client.post<Map<String, String>>("/test", mapOf("key" to "value"))
        assertTrue(response is SdkResult.Success)
        assertEquals(mapOf("status" to "created"), (response as SdkResult.Success).data)
    }

    @Test
    fun `post method without body handles success`() = runBlocking {
        val mockEngine = MockEngine {
            respond(
                content = "{}",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val client = createMockClient(mockEngine)
        val response = client.post<Unit>("/test")
        assertTrue(response is SdkResult.Success)
    }
}
