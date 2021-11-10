/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * Issue for a request.
 */
@Serializable
data class Issue(
    /**
     * Outcome issue level. One of the following enum elements: error, warning, information, fatal.
     */
    val severity: String,

    /**
     * Issue or Error code as defined in FHIR standards.
     */
    val code: String,

    /**
     * Additional details about the error.
     */
    val details: Details? = null,

    /**
     * Additional diagnostic information about the issue. NOTE - Do not use diagnostic message for
     * error handling purposes as the message is subject to change.
     */
    val diagnostics: String? = null,

    /**
     * Fields which are the cause of the error.
     */
    val location: List<String>? = null,
)
