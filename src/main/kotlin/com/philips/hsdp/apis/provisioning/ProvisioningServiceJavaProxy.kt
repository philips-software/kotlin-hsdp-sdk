/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning

import com.philips.hsdp.apis.provisioning.domain.sdk.DeviceIdentity
import com.philips.hsdp.apis.provisioning.domain.sdk.NewDeviceIdentity
import com.philips.hsdp.apis.provisioning.domain.sdk.ProvisioningResponse
import com.philips.hsdp.apis.support.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture

class ProvisioningServiceJavaProxy(provisioningUrl: String, httpClient: HttpClient) : AutoCloseable {
    private val provisioningService = ProvisioningService(provisioningUrl, httpClient)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun createIdentity(newDeviceIdentity: NewDeviceIdentity): CompletableFuture<ProvisioningResponse<DeviceIdentity>> =
        scope.future { provisioningService.createIdentity(newDeviceIdentity) }

    override fun close() {
        scope.cancel()
    }
}
