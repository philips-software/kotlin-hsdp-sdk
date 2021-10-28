/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk

import com.philips.hsdp.apis.tdr.domain.hsdp.Blob
import com.philips.hsdp.apis.tdr.domain.hsdp.Coding
import com.philips.hsdp.apis.tdr.domain.hsdp.Identifier
import com.philips.hsdp.apis.support.ValidationPatterns.iso8601UtcPattern
import com.thinkinglogic.builder.annotation.Builder
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Minimalistic representation of a TDR data item that is to be created/stored in TDR.
 *
 * HSDP wraps all data in bundles, bundle entries, and resources, whereas an application developer is typically
 * only interested in the data items. [NewDataItemDto] has non-nullable fields for mandatory parameters, and nullable
 * fields for optional parameters. Fields that are not allowed to be included when creating a data item are omitted
 * from [NewDataItemDto].
 *
 * For a detailed description of the fields, see [DataItem][com.philips.hsdp.apis.tdr.domain.hsdp.DataItem],
 * [Identifier][com.philips.hsdp.apis.tdr.domain.hsdp.Identifier], and
 * [Coding][com.philips.hsdp.apis.tdr.domain.hsdp.Coding] in the domain.hsdp package.
 */
@Serializable
@Builder
data class NewDataItemDto(
    // NOTE: I left out id and meta because of experience with new contracts, but it is not mentioned in the API spec
    // that those fields should not be passed in.
    val timestamp: String,
    val sequenceNumber: Int? = null,
    val device: Identifier? = null,
    val user: Identifier,
    val relatedPeripheral: Identifier? = null,
    val relatedUser: Identifier? = null,
    val dataType: Coding,
    val organization: String,
    val application: String? = null,
    val proposition: String? = null,
    val subscription: String? = null,
    val dataSource: String? = null,
    val dataCategory: String? = null,
    val data: JsonObject? = null,
    val blob: Blob? = null,
) {
    init {
        require(data != null || blob != null) {
            "At least one of the fields data and blob must be supplied"
        }
        require(iso8601UtcPattern.matches(timestamp)) {
            "Timestamp must be in ISO8601 format (including milliseconds)"
        }
    }
}
