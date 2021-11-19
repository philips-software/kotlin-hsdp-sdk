/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr.domain.sdk

import kotlinx.serialization.Serializable

/**
 * Data to be provided to a resource patch request by resource ID.
 */
@Serializable
data class PatchByIdRequest(
    /**
     * Type of resource to patch.
     */
    val resourceType: String,

    /**
     * The ID of the resource to patch.
     */
    val id: String,

    /**
     * String representation for the patch to a resource.
     */
    val body: String,

    /**
     * The format of the patch body.
     */
    val contentType: PatchContentType,

    /**
     * Indicate for which version this patch is intended (optimistic locking feature),
     * to make sure no (parallel) changes are lost.
     */
    val forVersion: String? = null,

    /**
     * Optional parameter to choose the response MIME type for clients that cannot modify the 'Accept' header.
     * Overrides the value given in the 'Accept' header.
     */
    val format: FormatParameter? = null,

    /**
     * Preference for the amount of data to be returned.
     */
    val preference: ReturnPreference? = null,
)
