/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.sdk

import com.philips.hsdp.apis.provisioning.domain.hsdp.Parameter
import com.thinkinglogic.builder.annotation.Builder
import kotlinx.serialization.Serializable

/**
 * Device attributes consists of a number of known (but optional) attributes, and for flexibility also some
 * additional attributes.
 */
@Serializable
@Builder
data class DeviceAttributes(
    val serialNumber: String? = null,
    val materialNumber: String? = null,
    val systemIdentifier: String? = null,
    val deviceExtId: DeviceExtId? = null,
    val additionalAttributes: List<Parameter>? = null,
)
