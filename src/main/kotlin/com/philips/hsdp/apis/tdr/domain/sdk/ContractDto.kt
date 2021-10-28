/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk

import com.philips.hsdp.apis.tdr.domain.hsdp.Coding
import com.philips.hsdp.apis.tdr.domain.hsdp.DeletePolicy
import com.philips.hsdp.apis.tdr.domain.hsdp.Meta
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Minimalistic representation of a TDR contract that is returned when getting contract(s).
 *
 * HSDP wraps all data in bundles, bundle entries, and resources, whereas an application developer is typically
 * only interested in the contracts. [ContractDto] uses non-nullable fields for mandatory parameters, and nullable
 * fields for optional parameters.
 *
 * For a detailed description of the fields, see [Contract][com.philips.hsdp.apis.tdr.domain.hsdp.Contract],
 * [Meta][com.philips.hsdp.apis.tdr.domain.hsdp.Meta], [Coding][com.philips.hsdp.apis.tdr.domain.hsdp.Coding], and
 * [DeletePolicy][com.philips.hsdp.apis.tdr.domain.hsdp.DeletePolicy] in the domain.hsdp package.
 */
@Serializable
data class ContractDto(
    val id: String?,
    val meta: Meta?,
    /**
     * As [schema] is a dynamic structure, it cannot be modelled with predefined classes.
     * Therefore, it is exposed as a JsonObject which can be (de)serialized (from)to JSON.
     */
    val schema: JsonObject,
    val dataType: Coding,
    val organization: String,
    val sendNotifications: Boolean?,
    val notificationServiceTopicId: String? = null,
    val deletePolicy: DeletePolicy,
    val link: SelfLinkDto?,
)

@Serializable
data class SelfLinkDto(
    val self: String,
)
