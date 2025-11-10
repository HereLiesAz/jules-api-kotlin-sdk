package com.jules.sdk

/**
 * A custom exception for reporting errors from the Jules API.
 *
 * This exception is thrown when an API request fails with a non-2xx response.
 * It includes the HTTP status code and the response body for debugging purposes.
 *
 * @property statusCode The HTTP status code of the failed response.
 * @property responseBody The body of the failed response.
 */
class JulesApiException(
    val statusCode: Int,
    val responseBody: String
) : RuntimeException("Jules API request failed with status code $statusCode: $responseBody")
