/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk

import kotlinx.serialization.Serializable

/**
 * Minimalistic representation of a list of TDR contracts that is returned when getting contracts.
 *
 * HSDP wraps all data in bundles, bundle entries, and resources, whereas an application developer is typically
 * only interested in the contracts.
 */
@Serializable
data class ContractsDto(
    /**
     * List of contracts for the current page.
     */
    val data: List<ContractDto>,
    /**
     * Pagination information for the current page (page size and page offset)
     */
    val pagination: PaginationDto,
    /**
     * Identifier that can be used for tracing requests in a flow.
     */
    val requestId: String,
)
