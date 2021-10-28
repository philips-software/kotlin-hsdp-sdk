/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * Link to a resource
 */
@Serializable
data class Link(
    /**
     * Relation of the resource to a current resource
     */
    val relation: String,

    /**
     * URL to the resource
     */
    val url: String,
)
