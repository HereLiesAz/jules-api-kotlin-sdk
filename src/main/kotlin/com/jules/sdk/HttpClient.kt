package com.jules.sdk

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Configuration for retrying failed HTTP requests.
 *
 * @property maxRetries The maximum number of times to retry a failed request.
 * @property initialDelayMs The initial delay in milliseconds before the first retry.
 */
data class RetryConfig(
    val maxRetries: Int = 3,
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
 * @property timeout The timeout for HTTP requests in milliseconds.
 * @property retryConfig The configuration for retrying failed requests.
 */
class HttpClient(
    private val apiKey: String,
    private val baseUrl: String = "https://jules.googleapis.com/v1alpha",
    private val timeout: Long = 30000,
    private val retryConfig: RetryConfig = RetryConfig()
) {
    val client = HttpClient(CIO) {
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
            level = LogLevel.ALL
        }
        install(HttpRequestRetry) {
            retryOnServerErrors(retryConfig.maxRetries)
            exponentialDelay(retryConfig.initialDelayMs.toDouble())
            retryIf { _, response ->
                response.status.value.let { it in setOf(408, 429, 500, 502, 503, 504) }
            }
        }
        defaultRequest {
            url(baseUrl)
            header("X-Goog-Api-Key", apiKey)
            contentType(ContentType.Application.Json)
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
        return client.get(endpoint) {
            params.forEach { (key, value) ->
                parameter(key, value)
            }
        }.body()
    }

    /**
     * Makes a POST request with a body and expects a response.
     *
     * @param T The expected return type.
     * @param endpoint The API endpoint to call.
     * @param body The request body.
     * @return The response body, deserialized to the expected type.
     */
    suspend inline fun <reified T> postAndReceive(endpoint: String, body: Any): T {
        return client.post(endpoint) {
            setBody(body)
        }.body()
    }

    /**
     * Makes a POST request with a body and does not expect a response.
     *
     * @param endpoint The API endpoint to call.
     * @param body The request body.
     */
    suspend fun postWithBody(endpoint: String, body: Any) {
        client.post(endpoint) {
            setBody(body)
        }
    }

    /**
     * Makes a POST request without a body and does not expect a response.
     *
     * @param endpoint The API endpoint to call.
     */
    suspend fun post(endpoint: String) {
        client.post(endpoint)
    }
}
