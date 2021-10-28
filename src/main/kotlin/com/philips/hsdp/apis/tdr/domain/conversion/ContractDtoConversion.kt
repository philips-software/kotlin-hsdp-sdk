/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.conversion

import com.philips.hsdp.apis.tdr.domain.hsdp.BundleEntry
import com.philips.hsdp.apis.tdr.domain.hsdp.Contract
import com.philips.hsdp.apis.tdr.domain.sdk.ContractDto
import com.philips.hsdp.apis.tdr.domain.sdk.SelfLinkDto

/**
 * Converts a BundleEntry returned by the HSDP TDR 'getContracts' endpoint to a ContractDto.
 */
fun BundleEntry.toContractDto(): ContractDto {
    val link: SelfLinkDto? = this.fullUrl?.let { SelfLinkDto(it) }
    return with(this.resource as Contract) {
        ContractDto(
            id = id,
            meta = meta,
            schema = schema,
            dataType = dataType,
            organization = organization,
            sendNotifications = sendNotifications,
            notificationServiceTopicId = notificationServiceTopicId,
            deletePolicy = deletePolicy,
            link = link,
        )
    }
}
