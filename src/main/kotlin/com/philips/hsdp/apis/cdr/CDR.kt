/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr

import com.philips.hsdp.apis.cdr.domain.sdk.*
import com.philips.hsdp.apis.support.HeaderParameter
import com.philips.hsdp.apis.support.HttpClient
import com.philips.hsdp.apis.support.logging.PlatformLoggerFactory
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.suspendCoroutine

/**
 * Clinical Data Repository (CDR) service.
 *
 * Not yet implemented features :
 * - getCapabilities
 * - performBatchOperation
 * - getHistory
 */
class CDR(
    private val cdrUrl: String,
    fhirVersion: String,
    mediaType: String = "application/fhir+json; charset=UTF-8",
    private val httpClient: HttpClient
) {
    private val logger = PlatformLoggerFactory.create(javaClass.simpleName, javaClass)
    private val versionedMediaType = "$mediaType; fhirVersion=$fhirVersion"

    /**
     * Read the current state of a FHIR resource.
     */
    suspend fun read(readRequest: ReadRequest): ReadResponse =
        read(prepareRequest(readRequest))

    /**
     * Read the state of a specific version of a FHIR resource.
     */
    suspend fun read(readVersionRequest: ReadVersionRequest): ReadResponse =
        read(prepareRequest(readVersionRequest))

    /**
     * Search a FHIR resource type based on some filter criteria.
     */
    suspend fun search(searchRequest: SearchRequest, searchMethod: SearchMethod): SearchResponse =
        search(prepareRequest(searchRequest, searchMethod))

    /**
     * Create a new FHIR resource with a server assigned ID
     */
    suspend fun create(createRequest: CreateRequest): CreateResponse =
        create(prepareRequest(createRequest))

    /**
     * Create a new FHIR batch or transaction operation
     */
    suspend fun createBatchOrTransaction(
        batchOrTransactionRequest: BatchOrTransactionRequest
    ): BatchOrTransactionResponse =
        createBatchOrTransaction(prepareRequest(batchOrTransactionRequest))

    /**
     * Delete a FHIR resource by ID.
     */
    suspend fun delete(deleteByIdRequest: DeleteByIdRequest): DeleteResponse =
        delete(prepareRequest(deleteByIdRequest))

    /**
     * Delete a FHIR resource by query.
     */
    suspend fun delete(deleteByQueryRequest: DeleteByQueryRequest): DeleteResponse =
        delete(prepareRequest(deleteByQueryRequest))

    /**
     * Update an existing FHIR resource by its ID (create if new).
     */
    suspend fun update(updateByIdRequest: UpdateByIdRequest): UpdateResponse =
        update(prepareRequest(updateByIdRequest))

    /**
     * Update an existing resource by query (create if new)
     */
    suspend fun update(updateByQueryRequest: UpdateByQueryRequest): UpdateResponse =
        update(prepareRequest(updateByQueryRequest))

    /**
     * Patch an existing FHIR resource by its ID.
     */
    suspend fun patch(patchByIdRequest: PatchByIdRequest): PatchResponse =
        patch(prepareRequest(patchByIdRequest))

    /**
     * Patch an existing FHIR resource by query.
     */
    suspend fun patch(patchByQueryRequest: PatchByQueryRequest): PatchResponse =
        patch(prepareRequest(patchByQueryRequest))


    private suspend fun read(request: Request): ReadResponse =
        suspendCoroutine { continuation ->
            httpClient.performRequest(request, continuation, logger) { response ->
                val eTag = getMandatoryHeaderValue(response, eTagHeader)
                val lastModified = getMandatoryHeaderValue(response, lastModifiedHeader)
                ReadResponse(
                    status = response.code,
                    body = requireNotNull(response.body?.string()),
                    versionId = toVersionId(eTag),
                    lastModified = toIso8601DateFormat(lastModified),
                )
            }
        }

    private suspend fun search(request: Request): SearchResponse =
        suspendCoroutine { continuation ->
            httpClient.performRequest(request, continuation, logger) { response ->
                SearchResponse(
                    status = response.code,
                    body = requireNotNull(response.body?.string()),
                )
            }
        }

    private suspend fun create(request: Request): CreateResponse =
        suspendCoroutine { continuation ->
            httpClient.performRequest(request, continuation, logger) { response ->
                val location = getMandatoryHeaderValue(response, locationHeader)
                val eTag = getMandatoryHeaderValue(response, eTagHeader)
                val lastModified = getMandatoryHeaderValue(response, lastModifiedHeader)
                CreateResponse(
                    status = response.code,
                    body = requireNotNull(response.body?.string()),
                    location = location,
                    versionId = toVersionId(eTag),
                    lastModified = toIso8601DateFormat(lastModified),
                )
            }
        }

    private suspend fun createBatchOrTransaction(request: Request): BatchOrTransactionResponse =
        suspendCoroutine { continuation ->
            httpClient.performRequest(request, continuation, logger) { response ->
                BatchOrTransactionResponse(
                    status = response.code,
                    body = requireNotNull(response.body?.string()),
                )
            }
        }

    private suspend fun delete(request: Request): DeleteResponse =
        suspendCoroutine { continuation ->
            httpClient.performRequest(request, continuation, logger) { response ->
                DeleteResponse(
                    status = response.code,
                    body = response.body?.string(),
                    versionId = response.headers[eTagHeader]?.let { toVersionId(it) },
                )
            }
        }

    private suspend fun update(request: Request): UpdateResponse =
        suspendCoroutine { continuation ->
            httpClient.performRequest(request, continuation, logger) { response ->
                val eTag = getMandatoryHeaderValue(response, eTagHeader)
                val lastModified = getMandatoryHeaderValue(response, lastModifiedHeader)
                UpdateResponse(
                    status = response.code,
                    body = requireNotNull(response.body?.string()),
                    location = response.headers[locationHeader],
                    versionId = toVersionId(eTag),
                    lastModified = toIso8601DateFormat(lastModified),
                )
            }
        }

    private suspend fun patch(request: Request): PatchResponse =
        suspendCoroutine { continuation ->
            httpClient.performRequest(request, continuation, logger) { response ->
                val eTag = getMandatoryHeaderValue(response, eTagHeader)
                val lastModified = getMandatoryHeaderValue(response, lastModifiedHeader)
                PatchResponse(
                    status = response.code,
                    body = requireNotNull(response.body?.string()),
                    location = response.headers[locationHeader],
                    versionId = toVersionId(eTag),
                    lastModified = toIso8601DateFormat(lastModified),
                )
            }
        }

    private fun prepareRequest(readRequest: ReadRequest): Request =
        with(readRequest) {
            buildRequest(
                pathSegments = "$resourceType/$id",
                headerParameters = listOfNotNull(
                    modifiedSinceTimestamp?.let { HeaderParameter(ifModifiedSinceHeader, it) },
                    modifiedSinceVersion?.let { HeaderParameter(ifNoneMatchHeader, toETag(it)) }
                ),
                queryParameters = listOfNotNull(
                    format?.let { QueryParameter(formatQuery, it.value) },
                    pretty?.let { QueryParameter(prettyQuery, it.toString()) },
                ),
            )
                .get()
                .build()
        }

    private fun prepareRequest(readVersionRequest: ReadVersionRequest): Request =
        with(readVersionRequest) {
            buildRequest(
                pathSegments = "$resourceType/$id/_history/$versionId",
                queryParameters = listOfNotNull(
                    format?.let { QueryParameter(formatQuery, it.value) },
                )
            )
                .get()
                .build()
        }

    private fun prepareRequest(searchRequest: SearchRequest, searchMethod: SearchMethod): Request =
        with(searchRequest) {
            when (searchMethod) {
                SearchMethod.Get ->
                    buildRequest(
                        pathSegments = compartment?.let { (type, id) ->
                            "$type/$id/$resourceType"
                        } ?: resourceType,
                        queryParameters = listOfNotNull(
                            format?.let { QueryParameter(formatQuery, it.value) },
                        ).plus(queryParameters?.map { QueryParameter(it.name, it.value) } ?: emptyList()),
                    )
                        .get()
                        .build()

                SearchMethod.Post -> {
                    val postBody = FormBody.Builder()
                    format?.let { postBody.add(formatQuery, it.value) }
                    queryParameters?.let { parameters ->
                        parameters.forEach { (name, value) -> postBody.add(name, value) }
                    }

                    buildRequest(
                        pathSegments = compartment?.let {
                            "${it.type}/${it.id}/$resourceType/_search"
                        } ?: "$resourceType/_search",
                    )
                        .post(postBody.build())
                        .build()
                }
            }
        }

    private fun prepareRequest(createRequest: CreateRequest): Request =
        with(createRequest) {
            buildRequest(
                pathSegments = resourceType,
                headerParameters = listOfNotNull(
                    validate?.let { HeaderParameter(xValidateResourceHeader, it.toString()) },
                    condition?.let { HeaderParameter(ifNoneExistsHeader, it) },
                    preference?.let { HeaderParameter(preferHeader, "return=${it.value}") },
                ),
                queryParameters = listOfNotNull(format?.let { QueryParameter(formatQuery, it.value) })
            )
                .post(body.toRequestBody(versionedMediaType.toMediaTypeOrNull()))
                .build()
        }

    private fun prepareRequest(batchOrTransactionRequest: BatchOrTransactionRequest): Request =
        with(batchOrTransactionRequest) {
            buildRequest(
                pathSegments = "",
                headerParameters = listOfNotNull(
                    preference?.let { HeaderParameter(preferHeader, "return=${it.value}") },
                ),
                queryParameters = listOfNotNull(format?.let { QueryParameter(formatQuery, it.value) })
            )
                .post(body.toRequestBody(versionedMediaType.toMediaTypeOrNull()))
                .build()
        }

    private fun prepareRequest(deleteByIdRequest: DeleteByIdRequest): Request =
        with(deleteByIdRequest) {
            buildRequest(
                pathSegments = "$resourceType/$id",
            )
                .delete()
                .build()
        }

    private fun prepareRequest(deleteByQueryRequest: DeleteByQueryRequest): Request =
        with(deleteByQueryRequest) {
            buildRequest(
                pathSegments = resourceType,
                queryParameters = queryParameters
            )
                .delete()
                .build()
        }

    private fun prepareRequest(updateByIdRequest: UpdateByIdRequest): Request =
        with(updateByIdRequest) {
            buildRequest(
                pathSegments = "$resourceType/$id",
                headerParameters = listOfNotNull(
                    forVersion?.let { HeaderParameter(ifMatchHeader, toETag(it)) },
                    validate?.let { HeaderParameter(xValidateResourceHeader, it.toString()) },
                    preference?.let { HeaderParameter(preferHeader, "return=${it.value}") },
                ),
                queryParameters = listOfNotNull(format?.let { QueryParameter(formatQuery, it.value) })
            )
                .put(body.toRequestBody(versionedMediaType.toMediaTypeOrNull()))
                .build()
        }

    private fun prepareRequest(updateByQueryRequest: UpdateByQueryRequest): Request =
        with(updateByQueryRequest) {
            buildRequest(
                pathSegments = resourceType,
                headerParameters = listOfNotNull(
                    forVersion?.let { HeaderParameter(ifMatchHeader, toETag(it)) },
                    preference?.let { HeaderParameter(preferHeader, "return=${it.value}") },
                ),
                queryParameters = listOfNotNull(
                    format?.let { QueryParameter(formatQuery, it.value) }
                ) + queryParameters
            )
                .put(body.toRequestBody(versionedMediaType.toMediaTypeOrNull()))
                .build()
        }

    private fun prepareRequest(patchByIdRequest: PatchByIdRequest): Request =
        with(patchByIdRequest) {
            buildRequest(
                pathSegments = "$resourceType/$id",
                headerParameters = listOfNotNull(
                    forVersion?.let { HeaderParameter(ifMatchHeader, toETag(it)) },
                    validate?.let { HeaderParameter(xValidateResourceHeader, it.toString()) },
                    preference?.let { HeaderParameter(preferHeader, "return=${it.value}") },
                ),
                queryParameters = listOfNotNull(format?.let { QueryParameter(formatQuery, it.value) })
            )
                .patch(body.toRequestBody(contentType.value.toMediaTypeOrNull()))
                .build()
        }

    private fun prepareRequest(patchByQueryRequest: PatchByQueryRequest): Request =
        with(patchByQueryRequest) {
            buildRequest(
                pathSegments = resourceType,
                headerParameters = listOfNotNull(
                    forVersion?.let { HeaderParameter(ifMatchHeader, toETag(it)) },
                    preference?.let { HeaderParameter(preferHeader, "return=${it.value}") },
                ),
                queryParameters = listOfNotNull(
                    format?.let { QueryParameter(formatQuery, it.value) }
                ) + queryParameters
            )
                .patch(body.toRequestBody(contentType.value.toMediaTypeOrNull()))
                .build()
        }

    private val eTagHeader = "ETag"
    private val lastModifiedHeader = "Last-Modified"
    private val locationHeader = "Location"
    private val ifModifiedSinceHeader = "If-Modified-Since"
    private val ifNoneMatchHeader = "If-None-Match"
    private val xValidateResourceHeader = "X-validate-resource"
    private val ifNoneExistsHeader = "If-None-Exists"
    private val preferHeader = "Prefer"
    private val ifMatchHeader = "If-Match"
    private val formatQuery = "_format"
    private val prettyQuery = "_pretty"

    private fun buildRequest(
        pathSegments: String,
        headerParameters: List<HeaderParameter> = emptyList(),
        queryParameters: List<QueryParameter>? = null
    ): Request.Builder {
        require(httpClient.token.accessToken.isNotEmpty()) {
            "An access token is required"
        }

        // Use OkHttp URL builder to make sure the path segments and query parameters are URL-encoded
        val urlBuilder = cdrUrl.toHttpUrl().newBuilder()
            .addPathSegments(pathSegments)
        queryParameters?.forEach { (key, value) ->
            urlBuilder.addQueryParameter(key, value)
        }

        val url = urlBuilder.build().toString()
        val requestBuilder = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer ${httpClient.token.accessToken}")
            .addHeader("Api-Version", "1")
            .addHeader("Accept", versionedMediaType)
        headerParameters.forEach { (name, value) ->
            requestBuilder.addHeader(name, value)
        }
        return requestBuilder
    }

    private fun getMandatoryHeaderValue(response: Response, headerName: String): String =
        response.headers[headerName].also {
            require(it != null) { "$headerName response header is missing" }
        }!!

    /**
     * Convert timestamps with format "Tue, 21 Sep 2021 17:11:39 GMT" to ISO8601 format ("2021-09-21T17:11:39Z")
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

    /**
     * The returned ETag header contains a W/" prefix and a " postfix that do not belong to the version id.
     */
    private fun toVersionId(eTag: String) =
        eTag.removePrefix("""W/"""").removeSuffix(""""""")

    /**
     * ETags have a W/" prefix and a " postfix appended to the version id.
     */
    private fun toETag(versionId: String) = """W/"$versionId""""

}