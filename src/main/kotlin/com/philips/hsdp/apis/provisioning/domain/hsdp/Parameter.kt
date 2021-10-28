/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning.domain.hsdp

import com.thinkinglogic.builder.annotation.Builder
import kotlinx.serialization.Serializable

/**
 * Parameter
 */
@Serializable
@Builder
data class Parameter(
    /**
     * Name of the parameter
     */
    val name: String,

    /**
     * Value of the parameter in case it is af integer type
     */
    val valueInteger: Int? = null,

    /**
     * Value of the parameter in case it is of long type
     */
    val valueDecimal: Long? = null,

    /**
     * Value of the parameter in case it is of ISO8601 DateTime string type (YYYY-MM-DDThh:mm:ss.sss[+-]hh:mm)
     */
    val valueDateTime: String? = null,

    /**
     * Value of the parameter in case it is of string type
     */
    val valueString: String? = null,

    /**
     * Value of the parameter in case it is of URI type
     */
    val valueUri: String? = null,

    /**
     * Value of the parameter in case it is of boolean type
     */
    val valueBoolean: Boolean? = null,

    /**
     * Value of the parameter in case it is of 'code' string type ([a-zA-Z0-9.-])
     */
    val valueCode: String? = null,

    /**
     * Value of the parameter in case it is of 'Identifier' type
     */
    val valueIdentifier: Identifier? = null,

    /**
     * Value of the parameter in case it is of 'Reference' type
     */
    val valueReference: Reference? = null,

    /**
     * Value of the parameter in case it is of 'Resource' type
     */
    val resource: Resource? = null,

    /**
     * Value of the parameter in case it is of 'part' type (containing a list of parameters)
     *
     * NOTE: this deviates from API spec, where it says this should be of nullable Parameters type
     */
    val part: List<Parameter>? = null,
)