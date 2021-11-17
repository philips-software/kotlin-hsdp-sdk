/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr

import com.philips.hsdp.apis.support.HeaderParameter
import com.philips.hsdp.apis.support.HttpClient
import com.philips.hsdp.apis.support.logging.PlatformLoggerFactory
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.suspendCoroutine

class CDR(private val cdrUrl: String, private val fhirVersion: String, private val httpClient: HttpClient) {
    private val logger = PlatformLoggerFactory.create(javaClass.simpleName, javaClass)
    private val jsonMediaType = "application/fhir+json; charset=UTF-8"

    suspend fun read(
        resourceType: String,
        resourceId: String,
        format: FormatParameter?  = null,
        pretty: Boolean? = null,
    ): CdrResponse =
        suspendCoroutine { continuation ->
            val queryParameters = mutableListOf<Pair<String, Any>>()
            format?.let { queryParameters.add("_format" to it.value) }
            pretty?.let { queryParameters.add("_pretty" to it) }
            val request = buildRequest(
                pathSegments = "$resourceType/$resourceId",
                queryParameters = queryParameters
            )
                .get()
                .build()

            httpClient.performRequest(request, continuation, logger) { response ->
                CdrResponse(
                    status = response.code,
                    jsonRepresentation = requireNotNull(response.body?.string()),
                    versionId = response.headers["ETag"]!!,
                    lastModified = toIso8601DateFormat(response.headers["Last-Modified"]!!)
                )
            }
        }

    suspend fun vread(resourceType: String, resourceId: String, versionId: String, queryParameters: List<Pair<String, String>>?  = null): String =
        suspendCoroutine { continuation ->
            val request = buildRequest(
                pathSegments = "$resourceType/$resourceId/_history/$versionId",
                queryParameters = queryParameters
            )
                .get()
                .build()

            httpClient.performRequest(request, continuation, logger) { response ->
                requireNotNull(response.body?.string())
            }
        }

    suspend fun searchViaGet(searchRequest: SearchRequest): CdrSearchResponse =
        suspendCoroutine { continuation ->
            with (searchRequest) {
                val allParams = mutableListOf<Pair<String, String>>()
                format?.let { allParams.add("_format" to it.value) }
                queryParameters?.let { allParams.addAll(it) }
                val request = buildRequest(
                    pathSegments = (compartment?.let{ "${it.type}/$it.id/" } ?: "") + resourceType,
                    queryParameters = allParams,
                )
                    .get()
                    .build()

                httpClient.performRequest(request, continuation, logger) { response ->
                    CdrSearchResponse(
                        status = response.code,
                        jsonRepresentation = requireNotNull(response.body?.string()),
                    )
                }
            }
        }

    suspend fun searchViaPost(searchRequest: SearchRequest): CdrSearchResponse =
        suspendCoroutine { continuation ->
            with(searchRequest) {
                val postBody = FormBody.Builder()
                format?.let { postBody.add("_format", it.value) }
                queryParameters?.let { parameters ->
                    parameters.forEach { (name, value) -> postBody.add(name, value) }
                }

                val request = buildRequest(
                    pathSegments = (compartment?.let{ "${it.type}/$it.id/" } ?: "") + "$resourceType/_search",
                )
                    .post(postBody.build())
                    .build()

                httpClient.performRequest(request, continuation, logger) { response ->
                    CdrSearchResponse(
                        status = response.code,
                        jsonRepresentation = requireNotNull(response.body?.string()),
                    )
                }
            }
        }

    private fun buildRequest(
        pathSegments: String,
        headerParameters: List<HeaderParameter> = emptyList(),
        queryParameters: List<Pair<String, Any>>? = null
    ): Request.Builder {
        require(httpClient.token.accessToken.isNotEmpty()) {
            "An access token is required"
        }

        // Use OkHttp URL builder to make sure the path segments and query parameters are URL-encoded
        val urlBuilder = cdrUrl.toHttpUrl().newBuilder()
            .addPathSegments(pathSegments)
        queryParameters?.forEach { (key, value) ->
            urlBuilder.addQueryParameter(key, value.toString())
        }

        val url = urlBuilder.build().toString()
        val requestBuilder = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer ${httpClient.token.accessToken}")
            .addHeader("Api-Version", "1")
//            .addHeader("Accept", "$jsonMediaType; fhirVersion=${fhirVersion}")
        headerParameters.forEach { (name, value) ->
            requestBuilder.addHeader(name, value)
        }
        return requestBuilder
    }

    /**
     * Convert timestamps with format "Tue, 21 Sep 2021 17:11:39 UTC" to ISO8601 format ("2021-09-21T17:11:39.000Z")
     *
     * NOTE: the returned timestamp in the header is on whole seconds, whereas the actual times in the resources are
     * in milliseconds. This can lead to confusion; same resource has different timestamp when obtained from response
     * header or response body. Would propose to return the millisecond based string, and if possible in ISO8601 format
     * (for consistency with other places where a timestamp is used).
     */
    private fun toIso8601DateFormat(timestamp: String): String {
        val date = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("EEE, dd LLL yyyy HH:mm:ss 'GMT'"))
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))
    }
}