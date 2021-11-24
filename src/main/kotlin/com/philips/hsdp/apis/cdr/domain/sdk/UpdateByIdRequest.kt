/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr.domain.sdk

import com.thinkinglogic.builder.annotation.Builder
import kotlinx.serialization.Serializable

/**
 * Data to be provided to a resource update request by resource ID.
 */
@Serializable
@Builder
data class UpdateByIdRequest(
    /**
     * Type of resource to update.
     */
    val resourceType: String,

    /**
     * The ID of the resource to update.
     */
    val id: String,

    /**
     * String representation for the update to a resource.
     */
    val body: String,

    /**
     * Indicate for which version this patch is intended (optimistic locking feature),
     * to make sure no (parallel) changes are lost.
     */
    val forVersion: String? = null,

    /**
     * If set to true, the resource is validated against the base StructureDefinition
     * and any profiles declared in meta.profile
     */
    val validate: Boolean? = null,

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
