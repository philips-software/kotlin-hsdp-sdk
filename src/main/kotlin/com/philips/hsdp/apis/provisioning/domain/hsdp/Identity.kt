/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.hsdp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents platform identity e.g. a device or an user client/agent.
 */
@Serializable
@SerialName("Identity")
data class Identity(
    /**
     * According to the HSDP API spec, the resourceType field should also be included in an Identity structure.
     * The deserialization requires a hint as to which data structure to decode to, and this is realized
     * by using the classDiscriminator, which will include the resourceType field to the serialized JSON
     * and use that to decode a JSON representation to an Identity structure.
     */

    override val id: String? = null,
    override val meta: Meta? = null,

    /**
     * A string that is associated with a single object or entity within a given system.
     * Typically, identifiers are used to connect content in resources to external content available in other
     * frameworks or protocols. In the data store, system and value are concatenated with a pipe '|'.
     */
    val identifier: Identifier,
) : Resource
