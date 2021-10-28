/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * Concept - reference to a terminology or just text.
 */
@Serializable
data class CodeableConcept(
    /**
     * Representation of a known value in some coding system.
     */
    val coding: List<Coding>,

    /**
     * Extra text to describe the concept
     */
    val text: String,
)
