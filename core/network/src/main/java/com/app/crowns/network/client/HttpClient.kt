package com.app.crowns.network.client

import com.app.crowns.network.NetworkResult
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer

interface HttpClient {
    suspend fun <T : Any> get(
        url: String,
        deserializer: DeserializationStrategy<T>,
        headers: Map<String, String> = emptyMap(),
        queryParams: Map<String, String> = emptyMap(),
    ): NetworkResult<T>

    suspend fun <B : Any, T : Any> post(
        url: String,
        body: B,
        serializer: SerializationStrategy<B>,
        deserializer: DeserializationStrategy<T>,
        headers: Map<String, String> = emptyMap(),
    ): NetworkResult<T>

    suspend fun <B : Any, T : Any> put(
        url: String,
        body: B,
        serializer: SerializationStrategy<B>,
        deserializer: DeserializationStrategy<T>,
        headers: Map<String, String> = emptyMap(),
    ): NetworkResult<T>

    suspend fun <T : Any> delete(
        url: String,
        deserializer: DeserializationStrategy<T>,
        headers: Map<String, String> = emptyMap(),
        queryParams: Map<String, String> = emptyMap(),
    ): NetworkResult<T>
}

suspend inline fun <reified T : Any> HttpClient.get(
    url: String,
    headers: Map<String, String> = emptyMap(),
    queryParams: Map<String, String> = emptyMap(),
): NetworkResult<T> = get(url, serializer<T>(), headers, queryParams)

suspend inline fun <reified B : Any, reified T : Any> HttpClient.post(
    url: String,
    body: B,
    headers: Map<String, String> = emptyMap(),
): NetworkResult<T> = post(url, body, serializer<B>(), serializer<T>(), headers)

suspend inline fun <reified B : Any, reified T : Any> HttpClient.put(
    url: String,
    body: B,
    headers: Map<String, String> = emptyMap(),
): NetworkResult<T> = put(url, body, serializer<B>(), serializer<T>(), headers)

suspend inline fun <reified T : Any> HttpClient.delete(
    url: String,
    headers: Map<String, String> = emptyMap(),
    queryParams: Map<String, String> = emptyMap(),
): NetworkResult<T> = delete(url, serializer<T>(), headers, queryParams)
