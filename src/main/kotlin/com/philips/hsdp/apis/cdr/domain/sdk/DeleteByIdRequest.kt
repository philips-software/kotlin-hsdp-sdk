/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr.domain.sdk

import kotlinx.serialization.Serializable

/**
 * Data to be provided to a resource deletion request by resource ID.
 */
@Serializable
data class DeleteByIdRequest(
    /**
     * Type of resource to delete.
     */
    val resourceType: String,

    /**
     * The ID of the resource to delete.
     */
    val id: String,
)