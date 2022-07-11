/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.oauth2

import com.philips.hsdp.apis.iam.oauth2.domain.conversion.toToken
import com.philips.hsdp.apis.iam.oauth2.domain.conversion.toTokenMetadata
import com.philips.hsdp.apis.iam.oauth2.domain.conversion.toUserInfo
import com.philips.hsdp.apis.iam.oauth2.domain.hsdp.IntrospectionResponse
import com.philips.hsdp.apis.iam.oauth2.domain.hsdp.TokenResponse
import com.philips.hsdp.apis.iam.oauth2.domain.hsdp.UserInfoResponse
import com.philips.hsdp.apis.iam.oauth2.domain.sdk.Token
import com.philips.hsdp.apis.iam.oauth2.domain.sdk.TokenMetadata
import com.philips.hsdp.apis.iam.oauth2.domain.sdk.UserInfo
import com.philips.hsdp.apis.support.HttpClient
import com.philips.hsdp.apis.support.HttpException
import com.philips.hsdp.apis.support.PrivateKeyReader
import com.philips.hsdp.apis.support.TokenRefresher
import com.philips.hsdp.apis.support.logging.PlatformLoggerFactory
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.util.Base64
import java.util.Calendar
import java.util.Date
import kotlin.coroutines.suspendCoroutine

/**
 * IAM OAuth2 service.
 */
class IamOAuth2(
    /**
     * Region information for access and identity management.
     */
    private val iamUrl: String,
    /**
     * Client ID to be used for endpoints that require basic authentication.
     */
    clientId: String,
    /**
     * Client Secret to be used for endpoints that require basic authentication.
     */
    clientSecret: String,
    /**
     * HTTP client that can automatically refresh a token if a call fails due to invalid or expired access token.
     */
    val httpClient: HttpClient,
    /**
     * Token to set during creation.
     *
     * If none is provided, an "empty" token is set, which means that user will first need to log in before
     * being able to call any HSDP service that requires authentication/authorization.
     */
    initialToken: Token = Token()
) : TokenRefresher {
    private val logger = PlatformLoggerFactory.create(javaClass.simpleName, javaClass)
    internal val client = httpClient.client
    private val basicAuthorization = Base64.getEncoder().encodeToString("$clientId:$clientSecret".toByteArray())

    @Volatile
    override var token: Token = initialToken
        private set

    private val jsonMediaType = "application/json; charset=utf-8"
    private val tokenPath = "authorize/oauth2/token"
    private val revokePath = "authorize/oauth2/revoke"
    private val introspectPath = "authorize/oauth2/introspect"
    private val userInfoPath = "authorize/oauth2/userinfo"

    enum class AuthorizationType {
        None,
        Basic,
        Bearer,
    }

    init {
        // Pass the IamOAuth2 instance as a token refresher to the HTTP client, so that latter
        // is able to access the IAM token and call the IAM refreshToken method.
        httpClient.tokenRefresher = this
    }

    private fun buildRequest(
        pathSegments: String,
        apiVersion: String,
        authorizationType: AuthorizationType = AuthorizationType.None,
    ): Request.Builder {
        // Use OkHttp URL builder to make sure the path segments and query parameters are URL-encoded
        val urlBuilder = iamUrl.toHttpUrl().newBuilder()
            .addPathSegments(pathSegments)

        val url = urlBuilder.build().toString()
        val requestBuilder = Request.Builder()
            .url(url)
            .addHeader("Api-Version", apiVersion)
            .addHeader("Accept", jsonMediaType)
        when (authorizationType) {
            AuthorizationType.Basic ->
                requestBuilder.addHeader("Authorization", "Basic $basicAuthorization")
            AuthorizationType.Bearer ->
                requestBuilder.addHeader("Authorization", "Bearer ${token.accessToken}")
            AuthorizationType.None -> {
                // Don't add any authorization header
            }
        }
        return requestBuilder
    }

    /**
     * Login with username and password to obtain a token. The stored basic auth information will be used.
     *
     * The received token will be stored and can be retrieved using the 'token' property.
     *
     * @param username Name of the user
     * @param password Password of the user
     * @throws SerializationException if the JSON received cannot be parsed
     * @throws HttpException if the HTTP call was not successful
     * @throws IOException if there was an issue that prevented the HTTP call being done at all
     * @throws IllegalArgumentException if the username or password is empty
     * @throws IllegalStateException if the basic authorization is not set
     * @return Token
     */
    suspend fun login(username: String, password: String): Token =
        suspendCoroutine { continuation ->
            require(username.isNotEmpty() && password.isNotEmpty())
            require(basicAuthorization.isNotEmpty())

            val postBody: RequestBody = FormBody.Builder()
                .addEncoded("grant_type", "password")
                .addEncoded("username", username)
                .addEncoded("password", password)
                .build()

            val request = buildRequest(tokenPath, "2", AuthorizationType.Basic)
                .post(postBody)
                .build()

            httpClient.performRequest(request, continuation, logger) { response ->
                val responseBody = requireNotNull(response.body?.string())
                token = Json.decodeFromString<TokenResponse>(responseBody).toToken()
                token
            }
        }

    /**
     * Login with client credentials to obtain a token. The stored basic auth information will be used.
     *
     * The received token will be stored and can be retrieved using the 'token' property.
     *
     * @throws SerializationException if the JSON received cannot be parsed
     * @throws HttpException if the HTTP call was not successful
     * @throws IOException if there was an issue that prevented the HTTP call being done at all
     * @throws IllegalArgumentException if the username or password is empty
     * @throws IllegalStateException if the basic authorization is not set
     * @return Token
     */
    suspend fun login(): Token =
        suspendCoroutine { continuation ->
            require(basicAuthorization.isNotEmpty())

            val postBody: RequestBody = FormBody.Builder()
                .addEncoded("grant_type", "client_credentials")
                .build()

            val request = buildRequest(tokenPath, "2", AuthorizationType.Basic)
                .post(postBody)
                .build()

            httpClient.performRequest(request, continuation, logger) { response ->
                val responseBody = requireNotNull(response.body?.string())
                token = Json.decodeFromString<TokenResponse>(responseBody).toToken()
                token
            }
        }

    /**
     * Login as a service to obtain a token.
     *
     * @param privateKey Private key, used to create a token for jwt-bearer grant_type.
     * @param iss Issuer, used to create a token for jwt-bearer grant_type.
     * @throws SerializationException if the JSON received cannot be parsed
     * @throws HttpException if the HTTP call was not successful
     * @throws IOException if there was an issue that prevented the HTTP call being done at all
     * @throws IllegalArgumentException if the username or password is empty
     * @throws IllegalStateException if the basic authorization is not set
     * @return Token
     */
    suspend fun serviceLogin(privateKey: String, iss: String): Token =
        suspendCoroutine { continuation ->
            val jwt = createJWT(privateKey, iss, iss, 60L * 1000)
            val postBody: RequestBody = FormBody.Builder()
                .addEncoded("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                .addEncoded("assertion", jwt)
                .build()

            val request = buildRequest(tokenPath, "2")
                .post(postBody)
                .build()

            httpClient.performRequest(request, continuation, logger) { response ->
                val responseBody = requireNotNull(response.body?.string())
                token = Json.decodeFromString<TokenResponse>(responseBody).toToken()
                token
            }
        }

    private fun createJWT(secret: String, issuer: String, subject: String, ttlMillis: Long): String {
        val privateKeyFromString = PrivateKeyReader(secret).read()
        return Jwts.builder()
            .signWith(SignatureAlgorithm.RS256, privateKeyFromString)
            .setAudience("$iamUrl/oauth2/access_token")
            .setExpiration(Date(Calendar.getInstance().time.time + ttlMillis))
            .setIssuer(issuer)
            .setSubject(subject)
            .compact()
    }

    /**
     * Refresh the current token
     *
     * @throws IllegalStateException if there is no refresh token available
     * @throws SerializationException if the JSON received cannot be parsed
     * @throws HttpException if the HTTP call was not successful
     * @throws IOException if there was an issue that prevented the HTTP call being done at all
     * @return Token
     */
    override suspend fun refreshToken(): Token =
        suspendCoroutine { continuation ->
            require(token.refreshToken.isNotEmpty()) {
                "The refresh token must not be empty."
            }

            val postBody: RequestBody = FormBody.Builder()
                .addEncoded("grant_type", "refresh_token")
                .addEncoded("refresh_token", token.refreshToken)
                .build()

            val request = buildRequest(tokenPath, "2", AuthorizationType.Basic)
                .post(postBody)
                .build()

            httpClient.performRequest(request, continuation, logger) { response ->
                val responseBody = requireNotNull(response.body?.string())
                val newToken: TokenResponse = Json.decodeFromString(responseBody)
                token = Token(
                    accessToken = newToken.accessToken,
                    refreshToken = token.refreshToken,
                    scopes = newToken.scopes,
                    expiresIn = newToken.expiresIn,
                    tokenType = newToken.tokenType,
                    idToken = token.idToken
                )
                token
            }
        }

    /**
     * Revoke the current token and invalidate the store token
     *
     * @throws SerializationException if the JSON received cannot be parsed
     * @throws HttpException if the HTTP call was not successful
     * @throws IOException if there was an issue that prevented the HTTP call being done at all
     * @throws IllegalStateException if the current token is not valid
     * @return Token
     */
    suspend fun revokeToken(): Token =
        suspendCoroutine { continuation ->
            require(token.isValid) {
                "The token must be valid."
            }

            val postBody: RequestBody = FormBody.Builder()
                .addEncoded("token", token.accessToken)
                .build()

            val request = buildRequest(revokePath, "2", AuthorizationType.Basic)
                .post(postBody)
                .build()

            httpClient.performRequest(request, continuation, logger) {
                token = Token(timestamp = 0)
                token
            }
        }

    /**
     * Introspect the current token
     *
     * @throws SerializationException if the JSON received cannot be parsed
     * @throws HttpException if the HTTP call was not successful
     * @throws IOException if there was an issue that prevented the HTTP call being done at all
     * @return Introspection results
     */
    suspend fun introspect(accessToken: String): TokenMetadata =
        suspendCoroutine { continuation ->
            require(accessToken.isNotEmpty()) {
                "Provided access token must not be empty."
            }

            val postBody: RequestBody = FormBody.Builder()
                .addEncoded("token", accessToken)
                .build()

            val request = buildRequest(introspectPath, "4", AuthorizationType.Basic)
                .post(postBody)
                .build()

            httpClient.performRequest(request, continuation, logger) { response ->
                val responseBody = requireNotNull(response.body?.string())
                Json.decodeFromString<IntrospectionResponse>(responseBody).toTokenMetadata()
            }
        }

    /**
     * Returns user information associated with the current access token
     *
     * @return User information
     */
    suspend fun userInfo(): UserInfo =
        suspendCoroutine { continuation ->
            require(token.accessToken.isNotEmpty()) {
                "The access token must not be empty."
            }

            val request = buildRequest(userInfoPath, "2", AuthorizationType.Bearer)
                .get()
                .build()

            httpClient.performRequest(request, continuation, logger) { response ->
                val responseBody = requireNotNull(response.body?.string())
                Json.decodeFromString<UserInfoResponse>(responseBody).toUserInfo()
            }
        }
}
