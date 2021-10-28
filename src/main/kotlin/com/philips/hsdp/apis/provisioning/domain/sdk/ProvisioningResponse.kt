/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.sdk

import kotlinx.serialization.Serializable

@Serializable
data class ProvisioningResponse<T>(
    val data: T,
    val transactionId: String? = null
)
