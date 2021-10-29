/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * A container for a collection of resources.
 */
@Serializable
data class BatchBundle(
    /**
     * The type of the resource represented as string.
     */
    val resourceType: ResourceType,

    /**
     * Indicates the purpose of this bundle. How it is intended to be used.
     */
    val type: Type,

    /**
     * Data items in the bundle.
     */
    val entry: List<BatchDataItem>,
)
