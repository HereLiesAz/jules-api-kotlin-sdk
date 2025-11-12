package com.hereliesaz.julesapisdk

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.io.Closeable

/**
 * Configuration for retrying failed HTTP requests.
 *
 * @property maxRetries The maximum number of times to retry a failed request.
 * @property initialDelayMs The initial delay in milliseconds before the first retry.
 */
data class RetryConfig(
    val maxRetries: Int = 0,
    val initialDelayMs: Long = 1000
)

/**
 * A wrapper around the Ktor HTTP client, configured for the Jules API.
 *
 * This class handles the details of making HTTP requests to the Jules API,
 * including authentication, content negotiation, and retry logic.
 *
 * @property apiKey The API key for authenticating with the Jules API.
 * @property baseUrl The base URL of the Jules API.
 * @property apiVersion The version of the Jules API.
 * @property timeout The timeout for HTTP requests in milliseconds.
 * @property retryConfig The configuration for retrying failed requests.
 * @property httpClient An optional pre-configured Ktor HttpClient.
 */
class JulesHttpClient(
    private val apiKey: String,
    private val baseUrl: String,
    private val apiVersion: String,
    private val timeout: Long = 30000,
    private val retryConfig: RetryConfig = RetryConfig(),
    private val httpClient: HttpClient? = null
) : Closeable {
    val client: HttpClient
    val fullUrl: String = "$baseUrl/$apiVersion"

    init {
        val baseClient = httpClient ?: HttpClient(CIO) {
            engine {
                requestTimeout = timeout
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.NONE
            }
            install(HttpRequestRetry) {
                retryOnServerErrors(retryConfig.maxRetries)
                exponentialDelay(retryConfig.initialDelayMs.toDouble())
                retryIf { _, response ->
                    response.status.value.let { it in setOf(408, 429, 500, 502, 503, 504) }
                }
                retryOnExceptionIf { _, cause ->
                    cause is java.io.IOException
                }
            }
        }

        client = baseClient.config {
            defaultRequest {
                header("X-Goog-Api-Key", apiKey)
                contentType(ContentType.Application.Json)
            }
        }
    }

    /**
     * Makes a GET request to the specified endpoint.
     *
     * @param T The expected return type.
     * @param endpoint The API endpoint to call.
     * @param params The query parameters for the request.
     * @return The response body, deserialized to the expected type.
     */
    suspend inline fun <reified T> get(endpoint: String, params: Map<String, String> = emptyMap()): T {
        val response = client.get(fullUrl + endpoint) {
            params.forEach { (key, value) ->
                parameter(key, value)
            }
        }
        if (!response.status.isSuccess()) {
            throw JulesApiException(response.status.value, response.body())
        }
        return response.body()
    }

    /**
     * Makes a POST request to the specified endpoint.
     *
     * @param T The expected return type.
     * @param endpoint The API endpoint to call.
     * @param body The optional request body. If null, no body is sent.
     * @return The response body, deserialized to the expected type.
     */
    suspend inline fun <reified T> post(endpoint: String, body: Any? = null): T {
        val response = client.post(fullUrl + endpoint) {
            if (body != null) {
                setBody(body)
            }
        }
        if (!response.status.isSuccess()) {
            throw JulesApiException(response.status.value, response.body())
        }
        return response.body()
    }

    override fun close() {
        client.close()
    }
}
