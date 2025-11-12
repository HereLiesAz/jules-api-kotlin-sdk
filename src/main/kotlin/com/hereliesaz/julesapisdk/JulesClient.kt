package com.hereliesaz.julesapisdk

import io.ktor.client.*
import java.io.Closeable

/**
 * A Kotlin client for the Jules AI API.
 *
 * This client provides a simple and convenient way to interact with the Jules API,
 * including methods for managing sources, sessions, and activities.
 *
 * @property httpClient The underlying HTTP client for making API requests.
 */
class JulesClient(
    private val httpClient: JulesHttpClient
) : Closeable {
    /**
     * Secondary constructor for creating a `JulesClient` with an API key.
     *
     * @param apiKey The API key for authenticating with the Jules API.
     * @param baseUrl The base URL of the Jules API. Defaults to "https://jules.googleapis.com".
     * @param apiVersion The version of the Jules API. Defaults to "v1alpha".
     * @param retryConfig The configuration for retrying failed requests. Defaults to a new `RetryConfig` instance.
     * @param ktorClient An optional pre-configured Ktor HttpClient.
     */
    constructor(
        apiKey: String,
        baseUrl: String = "https://jules.googleapis.com",
        apiVersion: String = "v1alpha",
        retryConfig: RetryConfig = RetryConfig(),
        ktorClient: HttpClient? = null
    ) : this(JulesHttpClient(apiKey, baseUrl, apiVersion, retryConfig = retryConfig, httpClient = ktorClient))

    /**
     * Lists all available sources.
     *
     * @param pageSize The maximum number of sources to return.
     * @param pageToken A token for pagination.
     * @param filter An optional AIP-160 filter expression (e.g., "name=sources/source1 OR name=sources/source2").
     * @return A `ListSourcesResponse` containing a list of sources and an optional next page token.
     */
    suspend fun listSources(pageSize: Int? = null, pageToken: String? = null, filter: String? = null): SdkResult<ListSourcesResponse> {
        val params = mutableMapOf<String, String>()
        pageSize?.let { params["pageSize"] = it.toString() }
        pageToken?.let { params["pageToken"] = it }
        filter?.let { params["filter"] = it }
        return httpClient.get("/sources", params)
    }

    /**
     * Gets a specific source by its ID.
     *
     * @param sourceId The ID of the source to retrieve.
     * @return The `Source` object.
     */
    suspend fun getSource(sourceId: String): SdkResult<GithubSource> {
        return httpClient.get("/sources/$sourceId")
    }

    /**
     * Creates a new session.
     *
     * @param request The request object for creating a session.
     * @return The created `JulesSession` object.
     */
    suspend fun createSession(request: CreateSessionRequest): SdkResult<JulesSession> {
        val result = httpClient.post<Session>("/sessions", request)
        return when (result) {
            is SdkResult.Success -> {
                // The create endpoint returns a partial session, so we need to fetch the full session
                // before returning it to the user.
                val session = getSession(result.data.id)
                when (session) {
                    is SdkResult.Success -> SdkResult.Success(JulesSession(this, session.data))
                    is SdkResult.Error -> session
                    is SdkResult.NetworkError -> session
                }
            }
            is SdkResult.Error -> result
            is SdkResult.NetworkError -> result
        }
    }

    /**
     * Lists all sessions.
     *
     * @param pageSize The maximum number of sessions to return.
     * @param pageToken A token for pagination.
     * @return A `ListSessionsResponse` containing a list of sessions and an optional next page token.
     */
    suspend fun listSessions(pageSize: Int? = null, pageToken: String? = null): SdkResult<ListSessionsResponse> {
        val params = mutableMapOf<String, String>()
        pageSize?.let { params["pageSize"] = it.toString() }
        pageToken?.let { params["pageToken"] = it }
        return httpClient.get("/sessions", params)
    }

    /**
     * Gets a specific session by its ID.
     *
        * @param sessionId The ID of the session to retrieve.
        * @return The `Session` object.
     */
    suspend fun getSession(sessionId: String): SdkResult<Session> {
        return httpClient.get("/sessions/$sessionId")
    }

    /**
     * Approves the latest plan for a session.
     *
     * @param sessionId The ID of the session.
     */
    suspend fun approvePlan(sessionId: String): SdkResult<Unit> {
        return httpClient.post("/sessions/$sessionId:approvePlan", ApprovePlanRequest())
    }

    /**
     * Lists all activities for a session.
     *
     * @param sessionId The ID of the session.
     * @param pageSize The maximum number of activities to return.
     * @param pageToken A token for pagination.
     * @return A `ListActivitiesResponse` containing a list of activities and an optional next page token.
     */
    suspend fun listActivities(sessionId: String, pageSize: Int? = null, pageToken: String? = null): SdkResult<ListActivitiesResponse> {
        val params = mutableMapOf<String, String>()
        pageSize?.let { params["pageSize"] = it.toString() }
        pageToken?.let { params["pageToken"] = it }
        return httpClient.get("/sessions/$sessionId/activities", params)
    }

    /**
     * Gets a specific activity for a session.
     *
     * @param sessionId The ID of the session.
     * @param activityId The ID of the activity to retrieve.
     * @return The `Activity` object.
     */
    suspend fun getActivity(sessionId: String, activityId: String): SdkResult<Activity> {
        return httpClient.get("/sessions/$sessionId/activities/$activityId")
    }

    /**
     * Sends a message to the agent in a session.
     *
     * @param sessionId The ID of the session.
     * @param prompt The prompt to send to the agent.
     * @return A `MessageResponse` object.
     */
    suspend fun sendMessage(sessionId: String, prompt: String): SdkResult<MessageResponse> {
        require(prompt.isNotBlank()) { "Prompt must be a non-empty string" }
        val request = SendMessageRequest(prompt)
        return httpClient.post("/sessions/$sessionId:sendMessage", request)
    }

    override fun close() {
        httpClient.close()
    }
}
