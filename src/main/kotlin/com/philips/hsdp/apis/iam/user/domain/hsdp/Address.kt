/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * Address of the user. http://hl7.org/fhir/2015May/datatypes.html#Address
 */
@Serializable
data class Address(
    /**
     * home | work | temp | old - purpose of this address
     */
    val use: AddressUse? = null,

    /**
     * Text representation of the address.
     */
    val text: String? = null,

    /**
     * Street name, number, direction & P.O. Box, etc.
     */
    val line: List<String>? = null,

    /**
     * Name of city, town, etc.
     */
    val city: String? = null,

    /**
     * Sub-unit of country (can include abbreviations).
     */
    val state: String? = null,

    /**
     * Postal code for area.
     */
    val postalCode: String? = null,

    /**
     * Country (can be ISO 3166 3-letter code).
     */
    val country: String? = null,
)
