/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr.domain.sdk

import com.thinkinglogic.builder.annotation.Builder
import kotlinx.serialization.Serializable

/**
 * Data to be provided to a resource read by resource ID for a given version ID.
 */
@Serializable
@Builder
data class ReadVersionRequest(
    /**
     * Type of resource to read.
     */
    val resourceType: String,

    /**
     * The ID of the resource to read.
     */
    val id: String,

    /**
     * The version ID of the resource to read.
     */
    val versionId: String,

    /**
     * Optional parameter to choose the response MIME type for clients that cannot modify the 'Accept' header.
     * Overrides the value given in the 'Accept' header.
     */
    val format: FormatParameter? = null,
)
