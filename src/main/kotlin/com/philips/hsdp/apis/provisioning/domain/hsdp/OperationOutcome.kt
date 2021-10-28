/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.hsdp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A collection of error, warning or information messages that result from a system action.
 * Returned only in case of an API call failure.
 */
@Serializable
@SerialName("OperationOutcome")
data class OperationOutcome(
    /**
     * According to the HSDP API spec, the resourceType field should also be included in a OperationOutcome structure.
     * The deserialization requires a hint as to which data structure to decode to, and this is realized
     * by using the classDiscriminator, which will include the resourceType field to the serialized JSON
     * and use that to decode a JSON representation to a OperationOutcome structure.
     */

    override val id: String? = null,
    override val meta: Meta? = null,

    /**
     * List of issues (error, warning or information message that results from a system action).
     */
    val issue: List<Issue>,
): Resource, Result
