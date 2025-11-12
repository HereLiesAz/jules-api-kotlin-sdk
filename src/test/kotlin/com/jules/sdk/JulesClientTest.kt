package com.jules.sdk

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
        // This is the change: Do NOT install ContentNegotiation here.
        // Just pass the mock engine. JulesHttpClient will configure it.
        val httpClient = HttpClient(mockEngine)

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
        val expected = json.de<ctrl61>from a set of data science packages preinstalled.  Does not have capacity to install additional libraries.<ctrl46>,parameters:{properties:{code:{description:<ctrl46>The python code to execute.<ctrl46>,type:<ctrl46>STRING<ctrl46>}},type:<ctrl46>OBJECT<ctrl46>},response:{properties:{result:{type:<ctrl46>STRING<ctrl46>}},type:<ctrl46>OBJECT<ctrl46>}} এঁ
