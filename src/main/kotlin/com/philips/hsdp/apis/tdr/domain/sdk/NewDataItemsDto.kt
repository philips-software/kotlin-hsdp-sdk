/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk

import kotlinx.serialization.Serializable

/**
 * Minimalistic representation of a list of TDR data items that is to be created/stored in TDR.
 *
 * HSDP wraps all data in bundles, bundle entries, and resources, whereas an application developer is typically
 * only interested in the data items.
 */
@Serializable
data class NewDataItemsDto(
    val dataItems: List<NewDataItemDto>
)
