# Jules AI Kotlin SDK

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

> **Note:** This is an **unofficial** community SDK for the Jules AI API. It is not affiliated with, officially maintained by, or endorsed by Google.

A fully-typed Kotlin SDK for the [Jules AI API](https://developers.google.com/jules/api), built with Ktor and Kotlinx Serialization. [Jules](https://jules.google.com) is a Google product that provides AI-powered coding assistance.

This SDK provides a simple, asynchronous, and type-safe way to interact with all Jules API endpoints from any Kotlin application.

**SDK Version:** 1.0.0 (implementing Jules API v1alpha, last updated 2025-11-10 UTC)

## Installation

Add the following dependency to your `build.gradle.kts` or `build.gradle` file:

**Gradle (Kotlin DSL)**
```kotlin
implementation("com.jules:jules-sdk:1.0.0")
```

**Gradle (Groovy DSL)**
```groovy
implementation 'com.jules:jules-sdk:1.0.0'
```

## Quick Start

The SDK is designed to be used with Kotlin Coroutines.

```kotlin
import com.jules.sdk.JulesClient
import com.jules.sdk.CreateSessionRequest
import com.jules.sdk.SourceContext
import com.jules.sdk.GithubRepoContext
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val client = JulesClient(
        apiKey = System.getenv("JULES_API_KEY") ?: "your-api-key"
    )

    // Create a session
    val sessionRequest = CreateSessionRequest(
        prompt = "Create a boba app!",
        sourceContext = SourceContext(
            source = "sources/github/owner/repo",
            githubRepoContext = GithubRepoContext(startingBranch = "main")
        ),
        title = "Boba App"
    )
    val session = client.createSession(sessionRequest)
    println("Created session: ${session.id}")


    // List activities for the session
    val activities = client.listActivities(session.id)
    println("Found ${activities.activities?.size ?: 0} activities.")

    // Send a message
    client.sendMessage(session.id, "Make it corgi themed!")
    println("Message sent!")
}
```

## API Methods

All methods are `suspend` functions and should be called from a coroutine.

- **Sources**: `listSources()`, `getSource(sourceId)`
- **Sessions**: `createSession(request)`, `listSessions()`, `getSession(sessionId)`, `approvePlan(sessionId)`
- **Activities**: `listActivities(sessionId)`, `getActivity(sessionId, activityId)`
- **Messages**: `sendMessage(sessionId, prompt)`

## API Reference

### JulesClient

#### Constructor

```kotlin
val client = JulesClient(
    apiKey: String,
    baseUrl: String = "https://jules.googleapis.com/v1alpha",
    retryConfig: RetryConfig = RetryConfig()
)
```

**Parameters:**
- `apiKey`: Your Jules API key (required).
- `baseUrl`: The base URL for the Jules API (optional, defaults to `https://jules.googleapis.com/v1alpha`).
- `retryConfig`: Configuration for request retries (optional).
  - `maxRetries`: Maximum number of retry attempts (default: 3).
  - `initialDelayMs`: The initial delay in milliseconds before the first retry (default: 1000).

**Example with all options:**
```kotlin
val client = JulesClient(
    apiKey = System.getenv("JULES_API_KEY")!!,
    baseUrl = "https://jules.googleapis.com/v1alpha",
    retryConfig = RetryConfig(
        maxRetries = 5,
        initialDelayMs = 500
    )
)
```

#### Methods

All methods are `suspend` functions.

##### `listSources(pageSize: Int?, pageToken: String?, filter: String?): ListSourcesResponse`
List all available sources.

##### `getSource(sourceId: String): Source`
Get details of a specific source.

##### `createSession(request: CreateSessionRequest): Session`
Create a new session.

##### `listSessions(pageSize: Int?, pageToken: String?): ListSessionsResponse`
List all sessions.

##### `getSession(sessionId: String): Session`
Get details of a specific session.

##### `approvePlan(sessionId: String)`
Approve the latest plan for a session.

##### `listActivities(sessionId: String, pageSize: Int?, pageToken: String?): ListActivitiesResponse`
List activities for a session.

##### `getActivity(sessionId: String, activityId: String): Activity`
Get a specific activity for a session.

##### `sendMessage(sessionId: String, prompt: String)`
Send a message to the agent in a session.

## Authentication

All API requests require authentication using your Jules API key. Get your API key from the Settings page in the [Jules web app](https://jules.google.com). The client automatically includes the key in the `X-Goog-Api-Key` header for all requests.

**Important:** Keep your API keys secure. Never commit them to version control or expose them in client-side code. Use environment variables or a secrets management tool.

## Error Handling & Resilience

The SDK is built on Ktor and provides robust error handling features.

### Automatic Retries

Retries are enabled by default for network failures and specific HTTP status codes (408, 429, 500, 502, 503, 504). You can customize the retry behavior:

```kotlin
val client = JulesClient(
    apiKey = System.getenv("JULES_API_KEY")!!,
    retryConfig = RetryConfig(
        maxRetries = 5,
        initialDelayMs = 500
    )
)
```

### Error Recovery

All API methods can throw exceptions (e.g., `ClientRequestException` for 4xx/5xx responses). Always wrap API calls in `try-catch` blocks.

```kotlin
try {
    val session = client.createSession(request)
    println("Success: ${session.id}")
} catch (e: Exception) {
    println("Error: ${e.message}")
}
```

## Resources

- [Official Jules API Documentation](https://developers.google.com/jules/api)
- [Jules Web App](https://jules.google.com)
- [GitHub Repository](https://github.com/kiwina/jules-api-sdk)

## Contributing

Contributions are welcome! Please read our [Contributing Guidelines](../CONTRIBUTING.md) for details.

## License

This project is licensed under the MIT License - see the [LICENSE](../LICENSE) file for details.
