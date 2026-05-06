package com.app.crowns.common

sealed class Result<out T, out E> {
    data class Success<T>(val data: T): Result<T, Nothing>()
    data class Failure<E>(val error: E): Result<Nothing, E>()
}

inline fun <T, E> Result<T, E>.onSuccess(block: (T) -> Unit): Result<T, E> {
    if (this is Result.Success) block(data)
    return this
}

inline fun <T, E> Result<T, E>.onFailure(block: (E) -> Unit): Result<T, E> {
    if (this is Result.Failure) block(error)
    return this
}