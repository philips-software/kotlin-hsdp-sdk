/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.conversion

import com.philips.hsdp.apis.tdr.domain.hsdp.BatchCreateResponseBundle
import com.philips.hsdp.apis.tdr.domain.hsdp.BatchCreatedResource
import com.philips.hsdp.apis.tdr.domain.hsdp.BatchCreationFailure
import com.philips.hsdp.apis.tdr.domain.sdk.CreatedDataItemsDto
import com.philips.hsdp.apis.tdr.domain.sdk.CreatedResource
import com.philips.hsdp.apis.tdr.domain.sdk.CreationFailure

/**
 * Converts a BatchCreateResponseBundle returned by the HSDP TDR 'storeDataItems' endpoint to a CreatedDataItemsDto.
 */
fun BatchCreateResponseBundle.toCreatedDataItemsDto(requestId: String): CreatedDataItemsDto {
    return CreatedDataItemsDto(
        dataItems = this.entry.map { entry ->
            when (entry) {
                is BatchCreatedResource -> CreatedResource(
                    location = entry.location,
                    etag = entry.etag,
                    lastModified = entry.lastModified,
                )
                is BatchCreationFailure -> CreationFailure(
                    issues = entry.outcome.issue
                )
            }
        },
        requestId = requestId,
    )
}
