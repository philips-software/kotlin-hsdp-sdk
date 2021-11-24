/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr.domain.sdk

import com.thinkinglogic.builder.annotation.Builder
import kotlinx.serialization.Serializable

/**
 * Data to be provided to a resource creation request.
 */
@Serializable
@Builder
data class CreateRequest(
    /**
     * Type of resource to create.
     */
    val resourceType: String,

    /**
     * String representation of the resource to create. Can be JSON or XML format.
     */
    val body: String,

    /**
     * If set to false, the resource validation against the base StructureDefinition
     * and any profiles declared in meta.profile is skipped. Default value is true.
     */
    val validate: Boolean? = null,

    /**
     * Optional parameter to choose the response MIME type for clients that cannot modify the Accept_header.
     * Overrides the value given in the 'Accept' header.
     */
    val format: FormatParameter? = null,

    /**
     * Indicates that the resource is only created if the condition specified in this header is not met.
     * If defined, will lead to an 'If-None-Exists' header with given expression.
     */
    val condition: String? = null,

    /**
     * Preference for the amount of data to be returned.
     */
    val preference: ReturnPreference? = null,
)