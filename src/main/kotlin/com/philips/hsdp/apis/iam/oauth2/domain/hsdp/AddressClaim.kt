/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.oauth2.domain.hsdp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * End-User's preferred postal address.
 *
 * The value of the address member is a JSON (RFC4627). Most of the time it will be just the full address
 * as a single string in the formatted sub-field structure.
 */
@Serializable
data class AddressClaim(
    /**
     * Full mailing address, formatted for display or use on a mailing label. This field MAY contain multiple lines,
     * separated by newlines. Newlines can be represented either as a carriage return/line feed pair ('\r\n') or as a
     * single line feed character ('\n').
     */
    @SerialName("formatted")
    val formatted: String = "",

    /**
     * Full street address component, which MAY include house number, street name, Post Office Box, and multi-line
     * extended street address information. This field MAY contain multiple lines, separated by newlines. Newlines can
     * be represented either as a carriage return/line feed pair ('\r\n') or as a single line feed character ('\n').
     */
    @SerialName("street_address")
    val streetAddress: String = "",

    /**
     * Zip code or postal code component.
     */
    @SerialName("postal_code")
    val postalCode: String = "",
)
