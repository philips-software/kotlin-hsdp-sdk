/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * A concept that may be defined by a formal reference to a terminology or ontology
 * or may be provided by text.
 */
@Serializable
data class CodeableConcept(
    /**
     * A human language representation of the concept as seen/selected/uttered by the user
     * who entered the data and/or which represents the intended meaning of the user.
     *
     * NOTE: according to API spec, this field is optional; does that make any sense when CodeableConcept is
     * already optional in its parent data class "Issue"?
     */
    val text: String,
)
