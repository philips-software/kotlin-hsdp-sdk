/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.oauth2.domain.hsdp

import com.philips.hsdp.apis.support.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Introspection response information.
 */
@Serializable
data class IntrospectionResponse(
    /**
     * Boolean indicator of whether the presented token is currently active.
     */
    @SerialName("active")
    val active: Boolean = false,

    /**
     * A JSON string containing a space-separated list of scopes associated with this token, in the format described
     * in Section 3.3 of OAuth 2.0 (RFC6749). When refresh_token is introspected, the value of scope will be _filtered_
     */
    @SerialName("scope")
    val scopes: String = "",

    /**
     * Client identifier for the OAuth 2.0 client that requested this token.
     */
    @SerialName("client_id")
    val clientId: String = "",

    /**
     * Human-readable login ID for the resource owner who authorized this token.
     */
    @SerialName("username")
    val userName: String = "",

    /**
     * Type of the token as defined in Section 5.1 of OAuth 2.0 `RFC6749`.
     */
    @SerialName("token_type")
    val tokenType: String = "",

    /**
     * Integer timestamp, measured in the number of seconds since January 1st 1970 UTC, indicating when
     * this token will expire, as defined in JWT `RFC7519`.
     */
    @SerialName("exp")
    val exp: Long = 0,

    /**
     * Subject of the token, as defined in JWT `RFC7519`. Usually a machine-readable identifier of the
     * resource owner who authorized this token.
     */
    @SerialName("sub")
    @Serializable(with = UUIDSerializer::class)
    val sub: UUID? = null,

    /**
     * String representing the issuer of this token, as defined in JWT.
     */
    @SerialName("iss")
    val iss: String = "",

    /**
     * An enumeration that indicates the type of identity. One of [ device, user, service, client ].
     */
    @SerialName("identity_type")
    val identityType: String = "",

    /**
     * If the identity is a device this string indicates what kind of device.
     */
    @SerialName("device_type")
    val deviceType: String = "",

    /**
     * The organizations in which the user is present.
     * If the 'refresh_token' is introspected then organizationList will be an empty array.
     */
    @SerialName("organizations")
    val organizations: Organizations? = null,

    /**
     * Type of the token as defined in Section 5.1 of OAuth 2.0 `RFC6749`.
     * This string indicates whether token submitted for introspection is access_token or refresh_token
     */
    @SerialName("token_type_hint")
    val tokenTypeHint: String = "",

    /**
     * Organization ID of client used for token generation.
     */
    @SerialName("client_organization_id")
    val clientOrganizationId: String = "",

    /**
     * Actor acting on behalf of subject as defined in `RFC8693`.
     * This is returned when token is granted for impersonation.
     */
    @SerialName("act")
    val actor: Actor? = null,
)
