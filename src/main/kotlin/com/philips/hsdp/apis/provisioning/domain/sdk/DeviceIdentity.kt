/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.sdk

import com.philips.hsdp.apis.provisioning.domain.hsdp.Parameter
import kotlinx.serialization.Serializable

/**
 * The device identity for a device, needed to connect to IAM and the IoT infrastructure.
 */
@Serializable
data class DeviceIdentity(
    val type: String,
    val identityType: String,
    val loginId: String,
    val password: String,
    val hsdpId: String,
    val oauthClientId: String,
    val oauthClientSecret: String,
    val identitySignature: String,
    val deviceAttributes: DeviceAttributes? = null,
    val additionalAttributes: List<Parameter>? = null,
)
