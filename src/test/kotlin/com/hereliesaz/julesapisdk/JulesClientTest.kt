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
            var path = request.url.encodedPath.removePrefix("/v1alpha")
            if (path.startsWith("/sessions/")) {
                path = path.replaceFirst("/sessions/", "/")
            }
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
        assertTrue(source is GithubRepoSource)
    }

    @Test
    fun `getSource returns source`() = runBlocking {
        val mockResponse = readResource("/getSource.json")
        client = createMockClient(mapOf("/sources/test-id" to mockResponse))
        val response = client.getSource("test-id")
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<GithubSource>(mockResponse)
        assertEquals(expected, (response as SdkResult.Success).data)
    }

    @Test
    fun `createSession returns session`() = runBlocking {
        val mockCreateResponse = readResource("/createSession.json")
        val mockGetResponse = readResource("/getSession.json")
        client = createMockClient(mapOf(
            "/sessions" to mockCreateResponse,
            "/test-id" to mockGetResponse
        ))
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
        val expected = json.decodeFromString<Session>(mockGetResponse)
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
        client = createMockClient(mapOf("/test-id" to mockResponse))
        val response = client.getSession("sessions/test-id")
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<Session>(mockResponse)
        assertEquals(expected, (response as SdkResult.Success).data)
    }

    @Test
    fun `approvePlan works`() = runBlocking {
        client = createMockClient(mapOf("/test-id:approvePlan" to "{}"))
        val response = client.approvePlan("sessions/test-id")
        assertTrue(response is SdkResult.Success)
    }

    @Test
    fun `listActivities returns activities`() = runBlocking {
        val mockResponse = readResource("/listActivities.json")
        client = createMockClient(mapOf("/test-id/activities" to mockResponse))
        val response = client.listActivities("sessions/test-id")
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<ListActivitiesResponse>(mockResponse)
        assertEquals(expected, (response as SdkResult.Success).data)
    }

    @Test
    fun `getActivity returns activity`() = runBlocking {
        val mockResponse = readResource("/getActivity.json")
        client = createMockClient(mapOf("/test-id/activities/activity-id" to mockResponse))
        val response = client.getActivity("sessions/test-id", "activity-id")
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<Activity>(mockResponse)
        assertEquals(expected, (response as SdkResult.Success).data)
    }

    @Test
    fun `sendMessage returns message`() = runBlocking {
        val mockResponse = readResource("/sendMessage_response.json")
        client = createMockClient(mapOf("/test-id:sendMessage" to mockResponse))
        val response = client.sendMessage("sessions/test-id", "prompt")
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<MessageResponse>(mockResponse)
        assertEquals(expected, (response as SdkResult.Success).data)
    }

    @Test
    fun `listSources returns empty list`() = runBlocking {
        val mockResponse = readResource("/listSources_response_empty.json")
        client = createMockClient(mapOf("/sources" to mockResponse))
        val response = client.listSources()
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<ListSourcesResponse>(mockResponse)
        assertEquals(expected, (response as SdkResult.Success).data)
    }

    @Test
    fun `listSources returns populated list`() = runBlocking {
        val mockResponse = readResource("/listSources_response_populated.json")
        client = createMockClient(mapOf("/sources" to mockResponse))
        val response = client.listSources()
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<ListSourcesResponse>(mockResponse)
        assertEquals(expected, (response as SdkResult.Success).data)
    }

    @Test
    fun `listSources returns paginated list`() = runBlocking {
        val mockResponse = readResource("/listSources_response_paginated.json")
        client = createMockClient(mapOf("/sources" to mockResponse))
        val response = client.listSources()
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<ListSourcesResponse>(mockResponse)
        assertEquals(expected, (response as SdkResult.Success).data)
    }

    @Test
    fun `createSession returns session with minimal request`() = runBlocking {
        val mockCreateResponse = readResource("/createSession_response.json")
        val mockGetResponse = readResource("/getSession_response_queued.json")
        client = createMockClient(mapOf(
            "/sessions" to mockCreateResponse,
            "/abc-123" to mockGetResponse
        ))
        val request = json.decodeFromString<CreateSessionRequest>(readResource("/createSession_request_minimal.json"))
        val response = client.createSession(request)
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<Session>(mockGetResponse)
        assertEquals(expected, (response as SdkResult.Success).data.session)
    }

    @Test
    fun `createSession returns session with maximal request`() = runBlocking {
        val mockCreateResponse = readResource("/createSession_response.json")
        val mockGetResponse = readResource("/getSession_response_queued.json")
        client = createMockClient(mapOf(
            "/sessions" to mockCreateResponse,
            "/abc-123" to mockGetResponse
        ))
        val request = json.decodeFromString<CreateSessionRequest>(readResource("/createSession_request_maximal.json"))
        val response = client.createSession(request)
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<Session>(mockGetResponse)
        assertEquals(expected, (response as SdkResult.Success).data.session)
    }

    @Test
    fun `getSession returns queued session`() = runBlocking {
        val mockResponse = readResource("/getSession_response_queued.json")
        client = createMockClient(mapOf("/test-id" to mockResponse))
        val response = client.getSession("sessions/test-id")
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<Session>(mockResponse)
        assertEquals(expected, (response as SdkResult.Success).data)
    }

    @Test
    fun `getSession returns awaiting approval session`() = runBlocking {
        val mockResponse = readResource("/getSession_response_awaiting_approval.json")
        client = createMockClient(mapOf("/test-id" to mockResponse))
        val response = client.getSession("sessions/test-id")
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<Session>(mockResponse)
        assertEquals(expected, (response as SdkResult.Success).data)
    }

    @Test
    fun `getSession returns completed with output session`() = runBlocking {
        val mockResponse = readResource("/getSession_response_completed_with_output.json")
        client = createMockClient(mapOf("/test-id" to mockResponse))
        val response = client.getSession("sessions/test-id")
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<Session>(mockResponse)
        assertEquals(expected, (response as SdkResult.Success).data)
    }

    @Test
    fun `getSession returns failed session`() = runBlocking {
        val mockResponse = readResource("/getSession_response_failed.json")
        client = createMockClient(mapOf("/test-id" to mockResponse))
        val response = client.getSession("sessions/test-id")
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<Session>(mockResponse)
        assertEquals(expected, (response as SdkResult.Success).data)
    }

    @Test
    fun `listSessions returns empty list`() = runBlocking {
        val mockResponse = readResource("/listSessions_response_empty.json")
        client = createMockClient(mapOf("/sessions" to mockResponse))
        val response = client.listSessions()
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<ListSessionsResponse>(mockResponse)
        assertEquals(expected, (response as SdkResult.Success).data)
    }

    @Test
    fun `listSessions returns populated list`() = runBlocking {
        val mockResponse = readResource("/listSessions_response_populated.json")
        client = createMockClient(mapOf("/sessions" to mockResponse))
        val response = client.listSessions()
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<ListSessionsResponse>(mockResponse)
        assertEquals(expected, (response as SdkResult.Success).data)
    }

    @Test
    fun `listSessions returns paginated list`() = runBlocking {
        val mockResponse = readResource("/listSessions_response_paginated.json")
        client = createMockClient(mapOf("/sessions" to mockResponse))
        val response = client.listSessions()
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<ListSessionsResponse>(mockResponse)
        assertEquals(expected, (response as SdkResult.Success).data)
    }

    @Test
    fun `approvePlan returns success`() = runBlocking {
        val mockResponse = readResource("/approvePlan_response.json")
        client = createMockClient(mapOf("/test-id:approvePlan" to mockResponse))
        val response = client.approvePlan("sessions/test-id")
        assertTrue(response is SdkResult.Success)
    }

    @Test
    fun `listActivities returns empty list`() = runBlocking {
        val mockResponse = readResource("/listActivities_response_empty.json")
        client = createMockClient(mapOf("/test-id/activities" to mockResponse))
        val response = client.listActivities("sessions/test-id")
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<ListActivitiesResponse>(mockResponse)
        assertEquals(expected, (response as SdkResult.Success).data)
    }

    @Test
    fun `listActivities returns paginated list`() = runBlocking {
        val mockResponse = readResource("/listActivities_response_paginated.json")
        client = createMockClient(mapOf("/test-id/activities" to mockResponse))
        val response = client.listActivities("sessions/test-id")
        assertTrue(response is SdkResult.Success)
        val expected = json.decodeFromString<ListActivitiesResponse>(mockResponse)
        assertEquals(expected, (response as SdkResult.Success).data)
    }
}
