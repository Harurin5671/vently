package com.app.crowns.network

import io.ktor.client.HttpClient
import com.app.crowns.network.client.HttpClient as HttpClientContract
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import javax.inject.Inject

class KtorHttpClient @Inject constructor(
    private val client: HttpClient,
    private val json: Json
) : HttpClientContract {

    override suspend fun <T : Any> get(
        url: String,
        deserializer: DeserializationStrategy<T>,
        headers: Map<String, String>,
        queryParams: Map<String, String>,
    ): NetworkResult<T> = safeCall {
        val response = client.get(url) {
            headers.forEach { (k, v) -> header(k, v) }
            queryParams.forEach { (k, v) -> parameter(k, v) }
        }
        json.decodeFromString(deserializer, response.bodyAsText())
    }

    override suspend fun <B : Any, T : Any> post(
        url: String,
        body: B,
        serializer: SerializationStrategy<B>,
        deserializer: DeserializationStrategy<T>,
        headers: Map<String, String>,
    ): NetworkResult<T> = safeCall {
        val response = client.post(url) {
            headers.forEach { (k, v) -> header(k, v) }
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(serializer, body))
        }
        json.decodeFromString(deserializer, response.bodyAsText())
    }

    override suspend fun <B : Any, T : Any> put(
        url: String,
        body: B,
        serializer: SerializationStrategy<B>,
        deserializer: DeserializationStrategy<T>,
        headers: Map<String, String>,
    ): NetworkResult<T> = safeCall {
        val response = client.put(url) {
            headers.forEach { (k, v) -> header(k, v) }
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(serializer, body))
        }
        json.decodeFromString(deserializer, response.bodyAsText())
    }

    override suspend fun <T : Any> delete(
        url: String,
        deserializer: DeserializationStrategy<T>,
        headers: Map<String, String>,
        queryParams: Map<String, String>,
    ): NetworkResult<T> = safeCall {
        val response = client.delete(url) {
            headers.forEach { (k, v) -> header(k, v) }
            queryParams.forEach { (k, v) -> parameter(k, v) }
        }
        json.decodeFromString(deserializer, response.bodyAsText())
    }
}
