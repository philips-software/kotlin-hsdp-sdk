/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A collection of error, warning or information messages that result from a system action.
 * Returned only in case of an API call failure.
 *
 * NOTE: resourceType is apparently not a mandatory field, but it is used here to support polymorphic (de)serialization
 * Ask HSDP if they can promote it to a mandatory field.
 */
@Serializable
@SerialName("OperationOutcome")
data class OperationOutcome(
    /**
     * The logical id of the resource. Once assigned, this value never changes.
     */
    override val id: String? = null,

    /**
     * The metadata about the resource. This is content that is maintained by the infrastructure.
     * Changes to the content may not always be associated with version changes to the resource.
     */
    override val meta: Meta? = null,

    /**
     * According to the HSDP API spec, the resourceType field should also be included in a Contract.
     * The deserialization requires a hint as to which data structure to decode to, and this is realized
     * by using the classDiscriminator, which will include the resourceType field to the serialized JSON
     * and use that to decode a JSON representation to a Contract.
     */

    /**
     * An error, warning or information message that results from a system action.
     */
    val issue: List<Issue>,
) : Resource
