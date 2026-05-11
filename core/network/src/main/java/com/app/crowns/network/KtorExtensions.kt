package com.app.crowns.network

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.serialization.SerializationException
import java.io.IOException

internal suspend fun <T> safeCall(block: suspend () -> T): NetworkResult<T> =
    try {
        NetworkResult.Success(block())
    } catch (e: RedirectResponseException) {
        NetworkResult.HttpError(
            code = e.response.status.value,
            message = "Redirect error: ${e.response.status.description}",
        )
    } catch (e: ClientRequestException) {
        when (e.response.status.value) {
            401 -> NetworkResult.Unauthorized
            else -> NetworkResult.HttpError(
                code = e.response.status.value,
                message = e.response.status.description,
            )
        }
    } catch (e: ServerResponseException) {
        NetworkResult.HttpError(
            code = e.response.status.value,
            message = "Server error: ${e.response.status.description}",
        )
    } catch (e: IOException) {
        NetworkResult.NetworkError(cause = e)
    } catch (e: SerializationException) {
        NetworkResult.NetworkError(cause = e)
    } catch (e: Exception) {
        NetworkResult.NetworkError(cause = e)
    }
