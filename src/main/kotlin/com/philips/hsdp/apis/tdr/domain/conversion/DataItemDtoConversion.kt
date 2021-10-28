/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.conversion

import com.philips.hsdp.apis.tdr.domain.hsdp.BundleEntry
import com.philips.hsdp.apis.tdr.domain.hsdp.DataItem
import com.philips.hsdp.apis.tdr.domain.sdk.DataItemDto
import com.philips.hsdp.apis.tdr.domain.sdk.SelfLinkDto

/**
 * Converts a BundleEntry returned by the HSDP TDR 'getDataItems' endpoint to a DataItemDto.
 */
fun BundleEntry.toDataItemDto(): DataItemDto {
    val link: SelfLinkDto? = this.fullUrl?.let { SelfLinkDto(it) }
    with(this.resource as DataItem) {
        return DataItemDto(
            id = id,
            meta = meta,
            timestamp = timestamp,
            sequenceNumber = sequenceNumber,
            device = device,
            user = user,
            relatedPeripheral = relatedPeripheral,
            relatedUser = relatedUser,
            dataType = dataType,
            organization = organization,
            application = application,
            proposition = proposition,
            subscription = subscription,
            dataSource = dataSource,
            dataCategory = dataCategory,
            data = data,
            blob = blob,
            deleteTimestamp = deleteTimestamp,
            creationTimestamp = creationTimestamp,
            tombstone = tombstone,
            link = link,
        )
    }
}
