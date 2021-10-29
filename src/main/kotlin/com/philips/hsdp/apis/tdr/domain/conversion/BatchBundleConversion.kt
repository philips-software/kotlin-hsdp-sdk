/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.conversion

import com.philips.hsdp.apis.tdr.domain.hsdp.BatchBundle
import com.philips.hsdp.apis.tdr.domain.hsdp.BatchDataItem
import com.philips.hsdp.apis.tdr.domain.hsdp.ResourceType
import com.philips.hsdp.apis.tdr.domain.hsdp.Type
import com.philips.hsdp.apis.tdr.domain.sdk.NewDataItemsDto

/**
 * Converts a NewDataItemsDto to a Batch bundle that is required by the HSDP TDR `storeDataItems` endpoint.
 */
fun NewDataItemsDto.toBatchBundle(): BatchBundle {
    return BatchBundle(
        resourceType = ResourceType.Bundle,
        type = Type.Batch,
        entry = dataItems.map { newDataItemDto ->
            with(newDataItemDto) {
                BatchDataItem(
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
                )
            }
        },
    )
}
