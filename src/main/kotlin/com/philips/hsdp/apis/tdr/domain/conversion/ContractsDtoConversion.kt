/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.conversion

import com.philips.hsdp.apis.tdr.domain.hsdp.Bundle
import com.philips.hsdp.apis.tdr.domain.hsdp.Contract
import com.philips.hsdp.apis.tdr.domain.sdk.ContractsDto
import com.philips.hsdp.apis.tdr.domain.sdk.PaginationDto

/**
 * Converts a Bundle with BundleEntries containing Contracts returned by the HSDP TDR 'getContracts' endpoint
 * to a ContractsDto.
 */
fun Bundle.toContractsDto(requestId: String): ContractsDto {
    val contracts = entry.filter { it.resource is Contract }
    val limitFromUrl = link
        ?.firstOrNull()
        ?.let { countRegex.matchEntire(it.url)?.groupValues?.getOrNull(1)?.toIntOrNull() }
    val actualLimit = limitFromUrl ?: defaultLimit
    return ContractsDto(
        data = contracts.map { it.toContractDto() },
        pagination = PaginationDto(
            offset = _startAt ?: defaultOffset,
            limit = actualLimit,
        ),
        requestId = requestId,
    )
}
