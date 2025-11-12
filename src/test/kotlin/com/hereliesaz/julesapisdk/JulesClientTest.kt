package com.hereliesaz.julesapisdk

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JulesClientTest {

    private lateinit var client: JulesClient
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

    private fun readResource(name: String): String {
        return this::class.java.getResource(name)!!.readText()
    }

    @Test
    fun `listSources returns sources`() = runBlocking {
        val mockResponse = readResource("/listSources.json")
        client = createMockClient(mapOf("/sources" to mockResponse))
        val response = client.listSources()
        val expected = json.decodeFromString<ListSourcesResponse>(mockResponse)
        assertEquals(expected, response)
    }

    @Test
    fun `getSource returns source`() = runBlocking {
        val mockResponse = readResource("/getSource.json")
        client = createMockClient(mapOf("/sources/test-id" to mockResponse))
        val response = client.getSource("test-id")
        val expected = json.decodeFromString<Source>(mockResponse)
        assertEquals(expected, response)
    }

    @Test
    fun `createSession returns session`() = runBlocking {
        val mockResponse = readResource("/createSession.json")
        client = createMockClient(mapOf("/sessions" to mockResponse))
        val response = client.createSession(CreateSessionRequest("prompt", SourceContext("source")))
        val expected = json.decodeFromString<Session>(mockResponse)
        assertEquals(expected, response)
    }

    @Test
    fun `listSessions returns sessions`() = runBlocking {
        val mockResponse = readResource("/listSessions.json")
        client = createMockClient(mapOf("/sessions" to mockResponse))
        val response = client.listSessions()
        val expected = json.decodeFromString<ListSessionsResponse>(mockResponse)
        assertEquals(expected, response)
    }

    @Test
    fun `getSession returns session`() = runBlocking {
        val mockResponse = readResource("/getSession.json")
        client = createMockClient(mapOf("/sessions/test-id" to mockResponse))
        val response = client.getSession("test-id")
        val expected = json.decodeFromString<Session>(mockResponse)
        assertEquals(expected, response)
    }

    @Test
    fun `approvePlan works`() = runBlocking {
        client = createMockClient(mapOf("/sessions/test-id:approvePlan" to "{}"))
        client.approvePlan("test-id")
    }

    @Test
    fun `listActivities returns activities`() = runBlocking {
        val mockResponse = readResource("/listActivities.json")
        client = createMockClient(mapOf("/sessions/test-id/activities" to mockResponse))
        val response = client.listActivities("test-id")
        val expected = json.decodeFromString<ListActivitiesResponse>(mockResponse)
        assertEquals(expected, response)
    }

    @Test
    fun `getActivity returns activity`() = runBlocking {
        val mockResponse = readResource("/getActivity.json")
        client = createMockClient(mapOf("/sessions/session-id/activities/activity-id" to mockResponse))
        val response = client.getActivity("session-id", "activity-id")
        val expected = json.decodeFromString<Activity>(mockResponse)
        assertEquals(expected, response)
    }

    @Test
    fun `sendMessage returns message`() = runBlocking {
        val mockResponse = readResource("/sendMessage.json")
        client = createMockClient(mapOf("/sessions/test-id:sendMessage" to mockResponse))
        val response = client.sendMessage("test-id", "prompt")
        val expected = json.decodeFromString<MessageResponse>(mockResponse)
        assertEquals(expected, response)
    }
}
