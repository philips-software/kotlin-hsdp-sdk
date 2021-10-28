/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.sdk

import kotlinx.serialization.Serializable

/**
 * TODO: obtain information for properly documenting this data structure.
 */
@Serializable
data class DeviceExtId(
    val system: String,
    val value: String,
    val type: Type,
)
