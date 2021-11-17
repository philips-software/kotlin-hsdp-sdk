/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning

import com.philips.hsdp.apis.provisioning.domain.conversion.toDeviceIdentity
import com.philips.hsdp.apis.provisioning.domain.conversion.toParameters
/* ktlint-disable no-wildcard-imports */
import com.philips.hsdp.apis.provisioning.domain.hsdp.*
/* ktlint-enable no-wildcard-imports */
import com.philips.hsdp.apis.provisioning.domain.sdk.DeviceIdentity
import com.philips.hsdp.apis.provisioning.domain.sdk.NewDeviceIdentity
import com.philips.hsdp.apis.provisioning.domain.sdk.ProvisioningResponse
import com.philips.hsdp.apis.support.HttpClient
import com.philips.hsdp.apis.support.logging.PlatformLoggerFactory
import kotlinx.serialization.SerializationException
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
import kotlin.coroutines.suspendCoroutine

/**
 * Module for accessing the Provisioning Service functionality of HSDP.
 *
 * The Provisioning Service allows for provisioning, reprovisioning, unprovisioning, resetting an identity and
 * creation of identity certificates for devices. All access to HSDP Provisioning Service is via REST calls,
 * which require a valid token obtained from IAM.
 *
 * Constructor injection is used to supply the module with the URL to a HSDP Provisioning service, and an
 * HTTP client which is configured for a certain IAM user to perform the requests to the HSDP Provisioning service.
 *
 * The domain models are divided in two parts, one matching the HSDP Provisioning API, and one matching the exposed
 * data structures for the HSDP Provisioning SDK. Latter takes away the burden of drilling down the various levels
 * of data structures to get to the meat. E.g. instead of returning a bundle to the user, with bundle entries, that
 * in turn contain (polymorphic) resources, which contain the actual information the user is interested in, the
 * module returns a.o. device identities as concepts.
 */
class ProvisioningService(private val provisioningUrl: String, private val httpClient: HttpClient) {
    private val logger = PlatformLoggerFactory.create(javaClass.simpleName, javaClass)

    /**
     * JSON serialization instance configured to cope with polymorphic resources in the bundles returned by
     * HSDP Provisioning API.
     */
    companion object {
        val json = Json {
            classDiscriminator = "resourceType"
            serializersModule = SerializersModule {
                polymorphic(Resource::class) {
                    subclass(Parameters::class)
                    subclass(CertificateRequest::class)
                    subclass(CertificateResponse::class)
                    subclass(Identity::class)
                    subclass(OperationOutcome::class)
                    subclass(BasicResource::class)
                }
            }
        }
    }

    // NOTE: adding "; charset=utf-8" to jsonMediaType results in error "Unsupported accept header"
    private val jsonMediaType = "application/json"
    private val createIdentityPath = "\$create-identity"

    private fun buildRequest(
        pathSegments: String,
//        queryParameters: List<QueryParameter> = emptyList()
    ): Request.Builder {
        require(httpClient.token.accessToken.isNotEmpty()) {
            "An access token is required"
        }

        // Use OkHttp URL builder to make sure the path segments and query parameters are URL-encoded
        val urlBuilder = provisioningUrl.toHttpUrl().newBuilder()
            .addPathSegments(pathSegments)
//        queryParameters.forEach { qp ->
//            urlBuilder.addQueryParameter(qp.name, qp.value)
//        }

        val url = urlBuilder.build().toString()
        return Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer ${httpClient.token.accessToken}")
            .addHeader("Api-Version", "1")
            .addHeader("Accept", jsonMediaType)
    }

    /**
     * Creates an identity in HSDP IAM and the HSDP DeviceCloud IoT registry.
     *
     * @param newDeviceIdentity The device identity to create
     * @return A data structure with device identity returned from HSDP TDR.
     */
    suspend fun createIdentity(newDeviceIdentity: NewDeviceIdentity): ProvisioningResponse<DeviceIdentity> =
        suspendCoroutine { continuation ->
            val body: RequestBody = json.encodeToString(newDeviceIdentity.toParameters() as Resource)
                .toRequestBody(jsonMediaType.toMediaTypeOrNull())

            val request = buildRequest(createIdentityPath)
                .post(body)
                .build()

            httpClient.performRequest(request, continuation, logger) { response ->
                val responseBody = requireNotNull(response.body?.string())
                val responseTransactionId = response.headers["transactionId"]
                val responseResource: Resource = json.decodeFromString(responseBody)
                val deviceIdentity = (responseResource as? Parameters)
                    ?.toDeviceIdentity()
                    ?: throw SerializationException("Response is not conforming to a device identity structure")
                ProvisioningResponse(data = deviceIdentity, transactionId = responseTransactionId)
            }
        }
}
