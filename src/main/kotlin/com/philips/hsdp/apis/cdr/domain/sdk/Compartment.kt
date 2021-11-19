/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr.domain.sdk

import kotlinx.serialization.Serializable

/**
 * A compartment is a logical grouping of resources which share a common property.
 *
 * Examples are Patient, Encounter, RelatedPerson, Practitioner, Device.
 */
@Serializable
data class Compartment(
    /**
     * Compartment resource type.
     */
    val type: String,
    /**
     * ID for the compartment resource.
     */
    val id: String,
)
