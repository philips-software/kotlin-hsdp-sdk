/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.sdk

import com.philips.hsdp.apis.provisioning.domain.hsdp.Parameter
import com.thinkinglogic.builder.annotation.Builder
import kotlinx.serialization.Serializable

/**
 * Information needed to create a new device identity.
 */
@Serializable
@Builder
data class NewDeviceIdentity(
    val type: String,
    /**
     * The type of identity to create; must be "device".
     */
    val identityType: String,
    val deviceAttributes: DeviceAttributes,
    val additionalAttributes: List<Parameter>? = null,
) {
    init {
        require(identityType == "device") {
            "identityType must be 'device'"
        }
    }
}
