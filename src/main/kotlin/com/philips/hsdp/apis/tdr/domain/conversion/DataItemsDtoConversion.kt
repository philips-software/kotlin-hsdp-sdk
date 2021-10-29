/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.conversion

import com.philips.hsdp.apis.tdr.domain.hsdp.Bundle
import com.philips.hsdp.apis.tdr.domain.hsdp.DataItem
import com.philips.hsdp.apis.tdr.domain.sdk.DataItemsDto
import com.philips.hsdp.apis.tdr.domain.sdk.PaginationDto

/**
 * Converts a Bundle with BundleEntries containing DataItems returned by the HSDP TDR 'getDataItems' endpoint
 * to a DataItemsDto.
 */
fun Bundle.toDataItemsDto(requestId: String): DataItemsDto {
    val dataItems = entry.filter { it.resource is DataItem }
    val limitFromUrl = link
        ?.firstOrNull()
        ?.let { countRegex.matchEntire(it.url)?.groupValues?.getOrNull(1)?.toIntOrNull() }
    val actualLimit = limitFromUrl ?: defaultLimit
    return DataItemsDto(
        data = dataItems.map { it.toDataItemDto() },
        pagination = PaginationDto(
            offset = _startAt ?: defaultOffset,
            limit = actualLimit,
        ),
        requestId = requestId,
    )
}
