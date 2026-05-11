package com.app.crowns.network

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class HttpError(val code: Int, val message: String) : NetworkResult<Nothing>()
    data class NetworkError(val cause: Throwable) : NetworkResult<Nothing>()
    data object Unauthorized : NetworkResult<Nothing>()
}

inline fun <T, R> NetworkResult<T>.fold(
    onSuccess: (T) -> R,
    onHttpError: (NetworkResult.HttpError) -> R,
    onNetworkError: (NetworkResult.NetworkError) -> R = { throw it.cause },
    onUnauthorized: () -> R = { throw IllegalStateException("Unauthorized") },
): R = when (this) {
    is NetworkResult.Success -> onSuccess(data)
    is NetworkResult.HttpError -> onHttpError(this)
    is NetworkResult.NetworkError -> onNetworkError(this)
    is NetworkResult.Unauthorized -> onUnauthorized()
}