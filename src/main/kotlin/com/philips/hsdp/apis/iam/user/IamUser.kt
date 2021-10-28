/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user

import com.philips.hsdp.apis.iam.oauth2.IamOAuth2
import com.philips.hsdp.apis.iam.user.domain.conversion.toUserList
import com.philips.hsdp.apis.iam.user.domain.hsdp.Users
import com.philips.hsdp.apis.iam.user.domain.sdk.User
import com.philips.hsdp.apis.support.HttpClient
import com.philips.hsdp.apis.support.logging.PlatformLoggerFactory
import com.philips.hsdp.apis.tdr.domain.sdk.query.QueryParameter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request
import kotlin.coroutines.suspendCoroutine

/**
 * IAM User Management.
 */
class IamUser(private val idmUrl: String, private val httpClient: HttpClient) {
    private val logger = PlatformLoggerFactory.create(javaClass.simpleName, javaClass)

    companion object {
        /**
         * JSON serialization instance configured to cope with polymorphic resources in the bundles returned by
         * HSDP TDR API.
         */
        val json = Json {
            ignoreUnknownKeys = true
        }
    }

    private val jsonMediaType = "application/json; charset=utf-8"
    private val requestIdHeader = "HSDP-Request-ID"
    private val searchUserPath = "authorize/identity/User"

    private fun buildRequest(
        pathSegments: String,
        apiVersion: String,
        authorizationType: IamOAuth2.AuthorizationType = IamOAuth2.AuthorizationType.None,
        queryParameters: List<QueryParameter> = emptyList(),
        requestId: String? = null,
    ): Request.Builder {
        // Use OkHttp URL builder to make sure the path segments and query parameters are URL-encoded
        val urlBuilder = idmUrl.toHttpUrl().newBuilder()
            .addPathSegments(pathSegments)
        queryParameters.forEach { qp ->
            urlBuilder.addQueryParameter(qp.name, qp.value)
        }

        val url = urlBuilder.build().toString()
        val requestBuilder = Request.Builder()
            .url(url)
            .addHeader("Api-Version", apiVersion)
            .addHeader("Accept", jsonMediaType)
        when (authorizationType) {
            IamOAuth2.AuthorizationType.Basic ->
                TODO("Implement basic authorization when new functionality needs it.")
            IamOAuth2.AuthorizationType.Bearer ->
                requestBuilder.addHeader("Authorization", "Bearer ${httpClient.token.accessToken}")
            IamOAuth2.AuthorizationType.None -> {
                // Don't add any authorization header
            }
        }
        if (requestId != null) {
            requestBuilder.addHeader(requestIdHeader, requestId)
        }
        return requestBuilder
    }

    /**
     * Search for a certain user.
     *
     * @param userId ID of the user.
     */
    suspend fun searchUser(userId: String, requestId: String? = null): List<User> =
        suspendCoroutine { continuation ->
            val queryParameters = listOf(
                QueryParameter("userId", userId),
                QueryParameter("profileType", "all")
            )

            val request = buildRequest(
                pathSegments = searchUserPath,
                apiVersion = "2",
                authorizationType = IamOAuth2.AuthorizationType.Bearer,
                queryParameters = queryParameters,
                requestId = requestId
            )
                .get()
                .build()

            httpClient.performRequest(request, continuation, logger) { _, responseBody ->
                json.decodeFromString<Users>(requireNotNull(responseBody)).toUserList()
            }
        }
}
