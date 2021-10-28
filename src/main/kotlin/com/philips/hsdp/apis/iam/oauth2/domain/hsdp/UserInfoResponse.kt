/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.oauth2.domain.hsdp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Information that is returned when calling the UserInfo endpoint.
 */
@Serializable
data class UserInfoResponse(
    /**
     * Required - Subject - Identifier for the End-User at the Issuer that the access token was granted.
     */
    @SerialName("sub")
    val sub: String,

    /**
     * End-User's full name in displayable form including all name parts, possibly including titles and suffixes,
     * ordered according to the End-User's locale and preferences.
     */
    @SerialName("name")
    val name: String = "",

    /**
     * Given name(s) or first name(s) of the End-User. Note that in some cultures, people can have multiple
     * given names; all can be present, with the names being separated by space characters.
     */
    @SerialName("given_name")
    val givenName: String = "",

    /**
     * Surname(s) or last name(s) of the End-User. Note that in some cultures, people can have multiple family
     * names or no family name; all can be present, with the names being separated by space characters.
     */
    @SerialName("family_name")
    val familyName: String = "",

    /**
     * End-User's preferred e-mail address. Its value MUST conform to the RFC 5322 `RFC5322` addr-spec syntax.
     * The RP MUST NOT rely upon this value being unique, as discussed in Section 5.7 Claim Stability and Uniqueness.
     */
    @SerialName("email")
    val email: String = "",

    /**
     * End-User's preferred postal address. The value of the address member is a JSON `RFC4627`. Most of the time
     * it will be just the full address as a single string in the formatted sub-field structure.
     */
    @SerialName("address")
    val address: AddressClaim? = null,

    /**
     * Time the End-User's information was last updated. Its value is a JSON number representing the number of
     * seconds from 1970-01-01T00:00:00Z as measured in UTC until the date/time.
     */
    @SerialName("updated_at")
    val updatedAt: Long = 0,
)