/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user

import com.philips.hsdp.apis.iam.user.domain.sdk.User
import com.philips.hsdp.apis.support.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture

class IamUserJavaProxy(idmUrl: String, httpClient: HttpClient): AutoCloseable {
    private val iamUser = IamUser(idmUrl, httpClient)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @JvmOverloads
    fun searchUser(userId: String, requestId: String? = null): CompletableFuture<List<User>> =
        scope.future { iamUser.searchUser(userId, requestId) }

    override fun close() {
        scope.cancel()
    }
}