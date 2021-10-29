/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.support

import com.philips.hsdp.apis.iam.oauth2.domain.sdk.Token
import com.philips.hsdp.apis.support.logging.PlatformLogger
import com.philips.hsdp.apis.support.logging.maskSensitiveHeaders
import kotlinx.coroutines.runBlocking
/* ktlint-disable no-wildcard-imports */
import okhttp3.*
/* ktlint-enable no-wildcard-imports */
import java.io.IOException
import java.time.Duration
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * HTTP client that embeds a token refresher, basically using part of IAM to obtain a token
 * and if needed refresh the token automatically.
 */
class HttpClient(callTimeout: Duration = Duration.ofSeconds(5)) : Authenticator {
    var tokenRefresher: TokenRefresher? = null

    val token: Token
        get() = tokenRefresher?.token ?: Token()

    /**
     * OkHttp client that is already provided with a token.
     *
     * Example of how to use this HttpClient in conjunction with IAM and e.g. TDR.
     * @sample
     * val httpClient = HttpClient()
     * val iam = IamOAuth2(region, clientId, clientSecret, httpClient)
     * val tdr = TDR(tdrUrl, httpClient)
     * val contracts = tdr.getContracts(...)
     * ...
     */
    val client = OkHttpClient().newBuilder()
        .authenticator(this)
        .callTimeout(callTimeout)
        .build()

    /**
     * Refreshes the token when the response indicates that the token is outdated or expired, and performs the request
     * again.
     */
    override fun authenticate(route: Route?, response: Response): Request? {
//        Timber.i("Authenticator called")

        if (!isRequestWithAccessToken(response)) {
            return null
        }

        val accessToken = retrieveAccessToken(response.request)

        synchronized(this) {
            return tokenRefresher?.let {
                val currentAccessToken = it.token.accessToken

                // See if the access token was refreshed in another thread
                val tokenToUse = if (accessToken != currentAccessToken && !it.token.isExpired) {
                    currentAccessToken
                } else {
                    // Otherwise, we need to refresh the access token
                    runBlocking {
                        it.refreshToken()
                    }
                    it.token.accessToken
                }

                response.request.newBuilder()
                    .header("Authorization", "Bearer $tokenToUse")
                    .build()
            }
        }
    }

    fun <T> performRequest(
        request: Request,
        continuation: Continuation<T>,
        logger: PlatformLogger,
        successResponseHandler: (responseHeaders: Headers, responseBody: String?) -> T
    ) {
        logger.debug { "${request.method} ${request.url}, headers: ${request.headers.maskSensitiveHeaders()}" }
        client
            .newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    // require(response.request.headers.get("Api-Version") == response.headers.get("Api-Version"))
                    val responseBody = response.body?.string()
//                    logger.debug { "Response ${response.code} $responseBody" }
                    // Disable logging of the body for now, as it would show e.g. DeviceIdentity
                    // or other sensitive data in the CI test logs.
                    // If the body fields would be masked in a similar way as the request header,
                    // then logging of the masked body could be enabled again.
                    logger.debug { "Response ${response.code}" }
                    if (response.isSuccessful) {
                        try {
                            val result = successResponseHandler(response.headers, responseBody)
                            continuation.resume(result)
                        } catch (e: Exception) {
                            logger.error { "Request failed: ${e.message}" }
                            continuation.resumeWithException(e)
                        }
                    } else {
                        continuation.resumeWithException(HttpException(response.code, responseBody ?: ""))
                    }
                }
            })
    }

    private fun retrieveAccessToken(request: Request): String {
        return request.header("Authorization")?.removePrefix("Bearer ") ?: ""
    }

    private fun isRequestWithAccessToken(response: Response): Boolean {
        return response.request.header("Authorization")?.startsWith("Bearer ") ?: false
    }
}
