/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk

import com.philips.hsdp.apis.tdr.domain.hsdp.Blob
import com.philips.hsdp.apis.tdr.domain.hsdp.Coding
import com.philips.hsdp.apis.tdr.domain.hsdp.Identifier
import com.philips.hsdp.apis.tdr.domain.hsdp.Meta
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Minimalistic representation of a TDR data item that is returned when getting data item(s).
 *
 * HSDP wraps all data in bundles, bundle entries, and resources, whereas an application developer is typically
 * only interested in the data items. [DataItemDto] uses non-nullable fields for mandatory parameters, and nullable
 * fields for optional parameters.
 *
 * For a detailed description of the fields, see [DataItem][com.philips.hsdp.apis.tdr.domain.hsdp.DataItem],
 * [Meta][com.philips.hsdp.apis.tdr.domain.hsdp.Meta], [Identifier][com.philips.hsdp.apis.tdr.domain.hsdp.Identifier],
 * and [Coding][com.philips.hsdp.apis.tdr.domain.hsdp.Coding] in the domain.hsdp package.
 */
@Serializable
data class DataItemDto(
    val id: String?,
    val meta: Meta?,
    val timestamp: String,
    val sequenceNumber: Int? = null,
    val device: Identifier? = null,
    val user: Identifier? = null,
    val relatedPeripheral: Identifier? = null,
    val relatedUser: Identifier? = null,
    val dataType: Coding,
    val organization: String,
    val application: String? = null,
    val proposition: String? = null,
    val subscription: String? = null,
    val dataSource: String? = null,
    val dataCategory: String? = null,
    /**
     * As [data] is a dynamic structure, it cannot be modelled with predefined classes.
     * Therefore, it is exposed as a JsonObject which can be (de)serialized (from)to JSON.
     */
    val data: JsonObject? = null,
    val blob: Blob? = null,
    val deleteTimestamp: String,
    val creationTimestamp: String,
    val tombstone: Boolean? = null,
    val link: SelfLinkDto?,
)
