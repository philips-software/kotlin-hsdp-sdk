/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * An error, warning or information message that results from a system action.
 */
@Serializable
data class Issue(
    /**
     * The severity level of the issue.
     */
    val severity: String,

    /**
     * Error or warning code.
     */
    val code: Code,

    /**
     * Concept - reference to a terminology or just text.
     */
    val details: CodeableConcept,

    /**
     * Additional diagnostic information about the issue. Must not be used in the production environment.
     */
    val diagnostics: String,
)