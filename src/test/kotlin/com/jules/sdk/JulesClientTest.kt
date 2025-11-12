package com.jules.sdk

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class JulesClientTest {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    private fun createMockClient(mockResponses: Map<String, String>): JulesClient {
        val mockEngine = MockEngine { request ->
            val responseContent = mockResponses[request.url.encodedPath] ?: ""
            respond(
                content = responseContent,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }
        return JulesClient(JulesHttpClient(apiKey = "test-key", httpClient = httpClient))
    }

    @Test
    fun `listSources returns sources`() = runBlocking {
        val mockResponse = ListSourcesResponse(sources = listOf(Source("name", "id", "now", "now", "url", "type")))
        val client = createMockClient(mapOf("/sources" to json.encodeToString(mockResponse)))
        val response = client.listSources()
        assertEquals(mockResponse, response)
    }

    @Test
    fun `getSource returns source`() = runBlocking {
        val mockResponse = Source("name", "id", "now", "now", "url", "type")
        val client = createMockClient(mapOf("/sources/test-id" to json.encodeToString(mockResponse)))
        val response = client.getSource("test-id")
        assertEquals(mockResponse, response)
    }

    @Test
    fun `createSession returns session`() = runBlocking {
        val mockResponse = Session("name", "id", "now", "now", SessionState.STATE_UNSPECIFIED, "url", "prompt", SourceContext("source"))
        val client = createMockClient(mapOf("/sessions" to json.encodeToString(mockResponse)))
        val response = client.createSession(CreateSessionRequest("prompt", SourceContext("source")))
        assertEquals(mockResponse, response)
    }

    @Test
    fun `listSessions returns sessions`() = runBlocking {
        val mockResponse = ListSessionsResponse(sessions = listOf(Session("name", "id", "now", "now", SessionState.STATE_UNSPECIFIED, "url", "prompt", SourceContext("source"))))
        val client = createMockClient(mapOf("/sessions" to json.encodeToString(mockResponse)))
        val response = client.listSessions()
        assertEquals(mockResponse, response)
    }

    @Test
    fun `getSession returns session`() = runBlocking {
        val mockResponse = Session("name", "id", "now", "now", SessionState.STATE_UNSPECIFIED, "url", "prompt", SourceContext("source"))
        val client = createMockClient(mapOf("/sessions/test-id" to json.encodeToString(mockResponse)))
        val response = client.getSession("test-id")
        assertEquals(mockResponse, response)
    }

    @Test
    fun `approvePlan works`() = runBlocking {
        val client = createMockClient(mapOf("/sessions/test-id:approvePlan" to "{}"))
        client.approvePlan("test-id")
    }

    @Test
    fun `listActivities returns activities`() = runBlocking {
        val mockResponse = ListActivitiesResponse(activities = listOf(Activity("id", "name", "desc", "now", "now", "prompt", "state")))
        val client = createMockClient(mapOf("/sessions/test-id/activities" to json.encodeToString(mockResponse)))
        val response = client.listActivities("test-id")
        assertEquals(mockResponse, response)
    }

    @Test
    fun `getActivity returns activity`() = runBlocking {
        val mockResponse = Activity("id", "name", "desc", "now", "now", "prompt", "state")
        val client = createMockClient(mapOf("/sessions/session-id/activities/activity-id" to json.encodeToString(mockResponse)))
        val response = client.getActivity("session-id", "activity-id")
        assertEquals(mockResponse, response)
    }

    @Test
    fun `sendMessage returns message`() = runBlocking {
        val mockResponse = MessageResponse("message")
        val client = createMockClient(mapOf("/sessions/test-id:sendMessage" to json.encodeToString(mockResponse)))
        val response = client.sendMessage("test-id", "prompt")
        assertEquals(mockResponse, response)
    }
}
