/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * An error, warning or information message that results from a system action.
 */
@Serializable
data class Issue(
    /**
     * The severity level of the issue.
     */
    val severity: Severity,

    /**
     * Error or warning code.
     */
    val code: IssueCode,

    /**
     * A concept that may be defined by a formal reference to a terminology or
     * ontology or may be provided by text.
     */
    val details: CodeableConcept? = null,

    /**
     * Additional diagnostic information about the issue.
     */
    val diagnostics: String? = null,
)
