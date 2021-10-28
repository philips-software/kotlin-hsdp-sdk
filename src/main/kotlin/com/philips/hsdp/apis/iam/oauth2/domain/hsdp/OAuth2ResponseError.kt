/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.oauth2.domain.hsdp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Describes the error that occurred in response to an OAuth2 request.
 */
@Serializable
data class OAuth2ResponseError(
    /**
     * As per RFC 6749, can be invalid_request, invalid_client, invalid_grant, unauthorized client,
     * un-supported grant type.
     */
    @SerialName("error")
    val error: String,

    /**
     * Description of the error.
     */
    @SerialName("error_description")
    val errorDescription: String,
)
