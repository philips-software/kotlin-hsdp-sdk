/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.hsdp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Parameters represent an array of objects of type Parameter. At least two parameters must be provided.
 * Maximally 32 parameters will be accepted. Total size of Parameters is 16 kB.
 * Only first level of Parameter can be of type Parameters (aka part)
 */
@Serializable
@SerialName("Parameters")
data class Parameters(
    /**
     * According to the HSDP API spec, the resourceType field should also be included in a Parameters structure.
     * The deserialization requires a hint as to which data structure to decode to, and this is realized
     * by using the classDiscriminator, which will include the resourceType field to the serialized JSON
     * and use that to decode a JSON representation to a Parameters structure.
     */

    override val id: String? = null,
    override val meta: Meta? = null,

    /**
     * List of parameters
     */
    val parameter: List<Parameter>,
) : Resource, Result {
    init {
        require(parameter.size in 2..32) {
            "The number of parameter entries should be between 2 and 32"
        }
        require(parameter.any { it.name == "type" && it.valueString != null }) {
            "The type parameter is required"
        }
        require(parameter.any { it.name == "identityType" && it.valueString == "device" }) {
            "The identityType parameter is required with the string value 'device'"
        }
    }
}
