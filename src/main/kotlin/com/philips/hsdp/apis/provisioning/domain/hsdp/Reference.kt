/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * Reference to another resource. This is either a local path (Device/12346fffeabcdef)
 * or a full URL (https://api.hsdp.com/connect/iam/Device/12346fffeabcdef).
 */
@Serializable
data class Reference(
    /**
     * The URL to another resource.
     */
    val reference: String,

    /**
     * Optional display name for the reference.
     */
    val display: String? = null,
)
