/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr

import com.philips.hsdp.apis.support.HttpClient
import com.philips.hsdp.apis.support.logging.PlatformLoggerFactory
import com.philips.hsdp.apis.tdr.domain.conversion.toBatchBundle
import com.philips.hsdp.apis.tdr.domain.conversion.toContractsDto
import com.philips.hsdp.apis.tdr.domain.conversion.toCreatedDataItemsDto
import com.philips.hsdp.apis.tdr.domain.conversion.toDataItemsDto
/* ktlint-disable no-wildcard-imports */
import com.philips.hsdp.apis.tdr.domain.hsdp.*
import com.philips.hsdp.apis.tdr.domain.sdk.*
import com.philips.hsdp.apis.tdr.domain.sdk.query.*
/* ktlint-enable no-wildcard-imports */
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.suspendCoroutine

/**
 * Module for accessing the Telemetry Data Repository functionality of HSDP.
 *
 * TDR allows for storage/retrieval of contracts and data items. All access to HSDP TDR is via REST calls, which
 * require a valid token obtained from IAM.
 *
 * Constructor injection is used to supply the module with the URL to a HSDP TDR service, and an HTTP client which
 * is configured for a certain IAM user to perform the requests to the HSDP TDR service.
 *
 * The domain models are divided in two parts, one matching the HSDP TDR API, and one matching the exposed data
 * structures for the HSDP TDR SDK. Latter takes away the burden of drilling down the various levels of data
 * structures to get to the meat. E.g. instead of returning a bundle to the user, with bundle entries, that in
 * turn contain (polymorphic) resources, which contain the actual information the user is interested in, the
 * module returns contracts and data items as concepts.
 */
class TDR(private val tdrUrl: String, private val httpClient: HttpClient) {
    private val logger = PlatformLoggerFactory.create(javaClass.simpleName, javaClass)

    companion object {
        /**
         * JSON serialization instance configured to cope with polymorphic resources in the bundles returned by
         * HSDP TDR API.
         */
        val json = Json {
            classDiscriminator = "resourceType"
            serializersModule = SerializersModule {
                polymorphic(Resource::class) {
                    subclass(Contract::class)
                    subclass(DataItem::class)
                }
            }
        }
    }

    private val jsonMediaType = "application/json; charset=utf-8"
    private val requestIdHeader = "HSDP-Request-ID"
    private val dataItemPath = "store/tdr/DataItem"
    private val dataItemsPath = "store/tdr/DataItems"
    private val contractPath = "store/tdr/Contract"

    private fun buildRequest(
        pathSegments: String,
        requestId: String?,
        queryParameters: List<QueryParameter> = emptyList()
    ): Request.Builder {
        require(httpClient.token.accessToken.isNotEmpty()) {
            "An access token is required"
        }

        // Use OkHttp URL builder to make sure the path segments and query parameters are URL-encoded
        val urlBuilder = tdrUrl.toHttpUrl().newBuilder()
            .addPathSegments(pathSegments)
        queryParameters.forEach { qp ->
            urlBuilder.addQueryParameter(qp.name, qp.value)
        }

        val url = urlBuilder.build().toString()
        val requestBuilder = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer ${httpClient.token.accessToken}")
            .addHeader("Api-Version", "5")
            .addHeader("Accept", jsonMediaType)
        if (requestId != null) {
            requestBuilder.addHeader(requestIdHeader, requestId)
        }
        return requestBuilder
    }

    /**
     * Searches for DataItems based on a set of query parameters.
     *
     * The HSDP TDR API allows a plethora of query possibilities for data items. Many of them are inter-related.
     * Those relations are enforced by "requiring" that certain conditions hold before even performing the query
     * to HSDP.
     *
     * @param query The set of different query parameters that can be used
     * @param requestId A unique id for the request that can be used for tracing in a microservice environment.
     *                  If none is supplied, HSDP will generate one for the response.
     * @return A data structure with all matching data items and a requestId as returned from HSDP TDR.
     */
    suspend fun getDataItems(query: DataItemQuery, requestId: String? = null): DataItemsDto =
        suspendCoroutine { continuation ->
            val request = buildRequest(dataItemPath, requestId, query.getQueryParameters())
                .get()
                .build()

            httpClient.performRequest(request, continuation, logger) { response ->
                val responseBody = requireNotNull(response.body?.string())
                val responseRequestId = response.headers[requestIdHeader]!!
                val bundle = json.decodeFromString(Bundle.serializer(), responseBody)
                bundle.toDataItemsDto(responseRequestId)
            }
        }

    /**
     * Stores a new data item in TDR.
     *
     * The response of HSDP TDR API is in the headers. The SDK will extract this information and returns it
     * to the user.
     *
     * @param dataItem The new data item to be stored.
     * @param requestId A unique id for the request that can be used for tracing in a microservice environment.
     *                  If none is supplied, HSDP will generate one for the response.
     * @return A data structure with the stored resource and a requestId as returned from HSDP TDR.
     */
    suspend fun storeDataItem(dataItem: NewDataItemDto, requestId: String? = null): CreatedResourceDto =
        suspendCoroutine { continuation ->
            val body: RequestBody = Json.encodeToString(dataItem)
                .toRequestBody(jsonMediaType.toMediaTypeOrNull())

            val request = buildRequest(dataItemPath, requestId)
                .post(body)
                .build()

            httpClient.performRequest(request, continuation, logger) { response ->
                CreatedResourceDto(
                    resource = CreatedResource(
                        location = response.headers["Location"]!!,
                        etag = response.headers["ETag"]!!,
                        lastModified = toIso8601DateFormat(response.headers["Last-Modified"]!!),
                    ),
                    requestId = response.headers[requestIdHeader]!!
                )
            }
        }

    /**
     * Patches an existing data item in TDR.
     *
     * The fields of an existing data item can be removed, replaced, moved, copied or tested,
     * or new fields can be added.
     *
     * @param query A set of query parameters that uniquely address a data item
     * @param patchDocuments A list of JSON patch documents (as defined by RFC 6902) to update the existing data item.
     * @param requestId A unique id for the request that can be used for tracing in a microservice environment.
     *                  If none is supplied, HSDP will generate one for the response.
     * @return A requestId as returned from HSDP TDR.
     */
    suspend fun patchDataItem(
        query: DataItemPatchQuery,
        patchDocuments: List<PatchDocument>,
        requestId: String? = null
    ): RequestIdDto =
        suspendCoroutine { continuation ->
            val body: RequestBody = Json.encodeToString(patchDocuments)
                .toRequestBody(jsonMediaType.toMediaTypeOrNull())

            val request = buildRequest(dataItemPath, requestId, query.getQueryParameters())
                .patch(body)
                .build()

            httpClient.performRequest(request, continuation, logger) { response ->
                RequestIdDto(response.headers[requestIdHeader]!!)
            }
        }

    /**
     * Deletes an existing data item in TDR.
     *
     * A data item that gets deleted will be "soft deleted", by setting the tombstone field to true.
     *
     * @param query A set of query parameters that results in a unique data item
     * @param requestId A unique id for the request that can be used for tracing in a microservice environment.
     *                  If none is supplied, HSDP will generate one for the response.
     * @return A requestId as returned from HSDP TDR.
     */
    suspend fun deleteDataItem(query: DataItemDeleteQuery, requestId: String? = null): RequestIdDto =
        suspendCoroutine { continuation ->
            val request = buildRequest(dataItemPath, requestId, query.getQueryParameters())
                .delete()
                .build()

            httpClient.performRequest(request, continuation, logger) { response ->
                RequestIdDto(response.headers[requestIdHeader]!!)
            }
        }

    /**
     * Stores a batch of new data items in TDR.
     *
     * The response of HSDP TDR API is in a special batch bundle. The SDK will extract this information and returns
     * a list of stored resources to the user.
     *
     * @param dataItems The new data items to be stored.
     * @param requestId A unique id for the request that can be used for tracing in a microservice environment.
     *                  If none is supplied, HSDP will generate one for the response.
     * @return A data structure with the stored resources and a requestId as returned from HSDP TDR.
     */
    suspend fun storeDataItems(dataItems: NewDataItemsDto, requestId: String? = null): CreatedDataItemsDto =
        suspendCoroutine { continuation ->
            val body: RequestBody = Json.encodeToString(dataItems.toBatchBundle())
                .toRequestBody(jsonMediaType.toMediaTypeOrNull())

            val request = buildRequest(dataItemsPath, requestId)
                .post(body)
                .build()

            httpClient.performRequest(request, continuation, logger) { response ->
                val responseBody = requireNotNull(response.body?.string())
                val responseBundle = Json.decodeFromString(BatchCreateResponseBundle.serializer(), responseBody)
                responseBundle.toCreatedDataItemsDto(response.headers[requestIdHeader]!!)
            }
        }

    /**
     * Stores a new contract in TDR.
     *
     * The response of HSDP TDR API is in the headers. The SDK will extract this information and returns it
     * to the user.
     *
     * @param contract The new contract to be stored.
     * @param requestId A unique id for the request that can be used for tracing in a microservice environment.
     *                  If none is supplied, HSDP will generate one for the response.
     * @return A data structure with the stored resource and a requestId as returned from HSDP TDR.
     */
    suspend fun storeContract(contract: NewContractDto, requestId: String? = null): CreatedResourceDto =
        suspendCoroutine { continuation ->
            val body: RequestBody = Json.encodeToString(contract)
                .toRequestBody(jsonMediaType.toMediaTypeOrNull())

            val request = buildRequest(contractPath, requestId)
                .post(body)
                .build()

            httpClient.performRequest(request, continuation, logger) { response ->
                CreatedResourceDto(
                    resource = CreatedResource(
                        location = response.headers["Location"]!!,
                        etag = response.headers["ETag"]!!,
                        lastModified = toIso8601DateFormat(response.headers["Last-Modified"]!!),
                    ),
                    requestId = response.headers[requestIdHeader]!!
                )
            }
        }

    /**
     * Searches for Contracts based on a set of query parameters.
     *
     * The HSDP TDR API allows a plethora of query possibilities for contracts. Some of them are inter-related.
     * Those relations are enforced by "requiring" that certain conditions hold before even performing the query
     * to HSDP.
     *
     * @param query The set of different query parameters that can be used
     * @param requestId A unique id for the request that can be used for tracing in a microservice environment.
     *                  If none is supplied, HSDP will generate one for the response.
     * @return A data structure with all matching contracts and a requestId as returned from HSDP TDR.
     */
    suspend fun getContracts(query: ContractQuery, requestId: String? = null): ContractsDto =
        suspendCoroutine { continuation ->
            val request = buildRequest(contractPath, requestId, query.getQueryParameters())
                .get()
                .build()

            httpClient.performRequest(request, continuation, logger) { response ->
                val responseBody = requireNotNull(response.body?.string())
                val responseRequestId = response.headers[requestIdHeader]!!
                val contractsBundle: Bundle = json.decodeFromString(responseBody)
                contractsBundle.toContractsDto(responseRequestId)
            }
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
        val date = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("EEE, dd LLL yyyy HH:mm:ss 'UTC'"))
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))
    }
}
