/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.hsdp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Base class for all resources.
 */
@Serializable
@SerialName("Resource")
data class BasicResource(
    /**
     * resourceType is provided by the polymorphic (de)serialization
     */

    /**
     * The logical ID of the resource.
     */
    override val id: String? = null,

    /**
     * Metadata about a resource.
     */
    override val meta: Meta? = null,
): Resource