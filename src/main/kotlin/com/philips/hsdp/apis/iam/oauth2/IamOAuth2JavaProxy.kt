/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.oauth2

import com.philips.hsdp.apis.iam.oauth2.domain.sdk.Token
import com.philips.hsdp.apis.iam.oauth2.domain.sdk.TokenMetadata
import com.philips.hsdp.apis.iam.oauth2.domain.sdk.UserInfo
import com.philips.hsdp.apis.support.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture

class IamOAuth2JavaProxy @JvmOverloads constructor(
    iamUrl: String,
    clientId: String,
    clientSecret: String,
    httpClient: HttpClient,
    initialToken: Token = Token()
): AutoCloseable {
    private val iamOAuth2: IamOAuth2 = IamOAuth2(iamUrl, clientId, clientSecret, httpClient, initialToken)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Is next annotation needed?
    // @Throws(SerializationException::class, IOException::class, HttpException::class)
    fun login(username: String, password: String): CompletableFuture<Token> =
        scope.future { iamOAuth2.login(username, password) }

    fun login(): CompletableFuture<Token> =
        scope.future { iamOAuth2.login() }

    fun serviceLogin(privateKey: String, iss: String): CompletableFuture<Token> =
        scope.future { iamOAuth2.serviceLogin(privateKey, iss) }

    fun refreshToken(): CompletableFuture<Token> =
        scope.future { iamOAuth2.refreshToken() }

    fun revokeToken(): CompletableFuture<Token> =
        scope.future { iamOAuth2.revokeToken() }

    fun introspect(accessToken: String): CompletableFuture<TokenMetadata> =
        scope.future { iamOAuth2.introspect(accessToken) }

    fun userInfo(): CompletableFuture<UserInfo> =
        scope.future { iamOAuth2.userInfo() }

    override fun close() {
        scope.cancel()
    }
}
