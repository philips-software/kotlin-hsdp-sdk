/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk

import kotlinx.serialization.Serializable

/**
 * Contains the pagination information for the returned page with contracts/data items.
 */
@Serializable
data class PaginationDto(
    /**
     * Offset page (first page has offset 0, second page 1, ...)
     */
    val offset: Int,

    /**
     * Number of items per page
     */
    val limit: Int,
)

