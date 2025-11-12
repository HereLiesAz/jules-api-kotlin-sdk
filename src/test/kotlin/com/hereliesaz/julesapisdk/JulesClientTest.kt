package com.hereliesaz.julesapisdk

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class JulesClientTest {

    private lateinit var client: JulesClient
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    private fun createMockClient(mockResponses: Map<String, String>): JulesClient {
        val mockEngine = MockEngine { request ->
            // The request path includes the api version, so we need to strip it
            val path = request.url.encodedPath.removePrefix("/v1alpha")
            val responseContent = mockResponses[path] ?: ""
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
        return JulesClient(apiKey = "test-key", ktorClient = httpClient)
    }

    private fun readResource(name: String): String {
        return this::class.java.getResource(name)!!.readText()
    }

    @Test
    fun `listSources returns sources`() = runBlocking {
        val mockResponse = readResource("/listSources.json")
        client = createMockClient(mapOf("/sources" to mockResponse))
        val response = client.listSources()
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<ListSourcesResponse>(mockResponse)
        assertEquals(expected, (response as SdkResult.Success).data)
    }

    @Test
    fun `listSources with correct data returns sources`() = runBlocking {
        val mockResponse = readResource("/listSources_correct.json")
        client = createMockClient(mapOf("/sources" to mockResponse))
        val response = client.listSources()
        assertTrue(response is SdkResult.Success)
        val data = (response as SdkResult.Success).data
        assertNotNull(data.sources)
        assertTrue(data.sources?.isNotEmpty() == true)
        val source = data.sources?.get(0)
        assertTrue(source is Source.GithubSource)
    }

    @Test
    fun `getSource returns source`() = runBlocking {
        val mockResponse = readResource("/getSource.json")
        client = createMockClient(mapOf("/sources/test-id" to mockResponse))
        val response = client.getSource("test-id")
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<Source>(mockResponse)
        assertEquals(expected, (response as SdkResult.Success).data)
    }

    @Test
    fun `createSession returns session`() = runBlocking {
        val mockResponse = readResource("/createSession.json")
        client = createMockClient(mapOf("/sessions" to mockResponse))
        val request = CreateSessionRequest(
            prompt = "Test prompt",
            sourceContext = SourceContext(
                source = "sources/github/test-owner/test-repo",
                githubRepoContext = GithubRepoContext(
                    startingBranch = "main"
                )
            ),
            title = "Test Session",
            requirePlanApproval = false,
            automationMode = AutomationMode.AUTO_CREATE_PR
        )
        val response = client.createSession(request)
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<Session>(mockResponse)
        assertEquals(expected, (response as SdkResult.Success).data.session)
    }

    @Test
    fun `listSessions returns sessions`() = runBlocking {
        val mockResponse = readResource("/listSessions.json")
        client = createMockClient(mapOf("/sessions" to mockResponse))
        val response = client.listSessions()
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<ListSessionsResponse>(mockResponse)
        assertEquals(expected, (response as SdkResult.Success).data)
    }

    @Test
    fun `getSession returns session`() = runBlocking {
        val mockResponse = readResource("/getSession.json")
        client = createMockClient(mapOf("/sessions/test-id" to mockResponse))
        val response = client.getSession("test-id")
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<Session>(mockResponse)
        assertEquals(expected, (response as SdkResult.Success).data)
    }

    @Test
    fun `approvePlan works`() = runBlocking {
        client = createMockClient(mapOf("/sessions/test-id:approvePlan" to "{}"))
        val response = client.approvePlan("test-id")
        assertTrue(response is SdkResult.Success)
    }

    @Test
    fun `listActivities returns activities`() = runBlocking {
        val mockResponse = readResource("/listActivities.json")
        client = createMockClient(mapOf("/sessions/test-id/activities" to mockResponse))
        val response = client.listActivities("test-id")
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<ListActivitiesResponse>(mockResponse)
        assertEquals(expected, (response as SdkResult.Success).data)
    }

    @Test
    fun `getActivity returns activity`() = runBlocking {
        val mockResponse = readResource("/getActivity.json")
        client = createMockClient(mapOf("/sessions/session-id/activities/activity-id" to mockResponse))
        val response = client.getActivity("session-id", "activity-id")
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<Activity>(mockResponse)
        assertEquals(expected, (response as SdkResult.Success).data)
    }

    @Test
    fun `sendMessage returns message`() = runBlocking {
        val mockResponse = readResource("/sendMessage.json")
        client = createMockClient(mapOf("/sessions/test-id:sendMessage" to mockResponse))
        val response = client.sendMessage("test-id", "prompt")
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<MessageResponse>(mockResponse)
        assertEquals(expected, (response as SdkResult.Success).data)
    }
}
