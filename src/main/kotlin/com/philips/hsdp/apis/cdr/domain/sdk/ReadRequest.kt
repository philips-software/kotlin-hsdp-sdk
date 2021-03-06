/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr.domain.sdk

import com.thinkinglogic.builder.annotation.Builder
import kotlinx.serialization.Serializable

/**
 * Data to be provided to a resource read by resource ID.
 */
@Serializable
@Builder
data class ReadRequest(
    /**
     * Type of resource to read.
     */
    val resourceType: String,

    /**
     * The ID of the resource to read.
     */
    val id: String,

    /**
     * Indicates that a resource be returned only if modified from the given date/time.
     */
    val modifiedSinceTimestamp: String? = null,

    /**
     * Indicate that a resource be returned only if the resource had undergone changes since the given versionId.
     */
    val modifiedSinceVersion: String? = null,

    /**
     * Optional parameter to choose the response MIME type for clients that cannot modify the 'Accept' header.
     * Overrides the value given in the 'Accept' header.
     */
    val format: FormatParameter? = null,

    /**
     * Development option to beautify the returned body.
     */
    val pretty: Boolean? = null,
)
