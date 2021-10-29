/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk

import com.philips.hsdp.apis.tdr.domain.hsdp.Coding
import com.philips.hsdp.apis.tdr.domain.hsdp.DeletePolicy
import com.thinkinglogic.builder.annotation.Builder
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Minimalistic representation of a TDR contract that is to be created/stored in TDR.
 *
 * HSDP wraps all data in bundles, bundle entries, and resources, whereas an application developer is typically
 * only interested in the contracts. [NewContractDto] has non-nullable fields for mandatory parameters, and nullable
 * fields for optional parameters. Fields that are not allowed to be included when creating a contract are omitted
 * from [NewContractDto].
 *
 * For a detailed description of the fields, see [Contract][com.philips.hsdp.apis.tdr.domain.hsdp.Contract],
 * [Coding][com.philips.hsdp.apis.tdr.domain.hsdp.Coding], and
 * [DeletePolicy][com.philips.hsdp.apis.tdr.domain.hsdp.DeletePolicy] in the domain.hsdp package.
 *
 *
 */
@Serializable
@Builder
data class NewContractDto(
    /**
     * As [schema] is a dynamic structure, it cannot be modelled with predefined classes.
     * Therefore, it is exposed as a JsonObject which can be (de)serialized (from)to JSON.
     */
    val schema: JsonObject,
    val dataType: Coding,
    val organization: String,
    val sendNotifications: Boolean? = null,
    val deletePolicy: DeletePolicy,
)
