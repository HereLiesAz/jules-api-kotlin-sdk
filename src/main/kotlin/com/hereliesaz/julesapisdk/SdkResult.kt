package com.hereliesaz.julesapisdk

/**
 * A sealed class representing the result of an SDK operation.
 *
 * @param T The type of the successful result.
 */
sealed class SdkResult<out T> {
    /**
     * Represents a successful result.
     *
     * @param T The type of the successful result.
     * @property data The data returned by the successful operation.
     */
    data class Success<T>(val data: T) : SdkResult<T>()

    /**
     * Represents an error result from the API.
     *
     * @property code The HTTP status code of the error.
     * @property body The body of the error response.
     */
    data class Error(val code: Int, val body: String) : SdkResult<Nothing>()

    /**
     * Represents a network error.
     *
     * @property throwable The throwable that caused the network error.
     */
    data class NetworkError(val throwable: Throwable) : SdkResult<Nothing>()
}
