/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk

import kotlinx.serialization.Serializable

/**
 * Minimalistic representation of a list of TDR data items that is returned when getting data items.
 *
 * HSDP wraps all data in bundles, bundle entries, and resources, whereas an application developer is typically
 * only interested in the data items.
 */
@Serializable
data class DataItemsDto(
    /**
     * List of data items for the current page.
     */
    val data: List<DataItemDto>,
    /**
     * Pagination information for the page (page size and page offset)
     */
    val pagination: PaginationDto,
    /**
     * Identifier that can be used for tracing requests in a flow.
     */
    val requestId: String,
)
