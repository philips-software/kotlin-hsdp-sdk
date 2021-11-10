/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.oauth2.domain.hsdp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Token information for a logged in identity.
 */
@Serializable
data class TokenResponse(
    /**
     * This contains scope valid for that client authorization. Max length depends on the number of scopes
     * associated with the client application. It is recommended to limit the size of one scope value to 256.
     * It is recommended to limit the number of scopes per client to 1000.
     */
    @SerialName("scope")
    val scopes: String = "",

    /**
     * As of the present implementation, only the bearer token type is supported.
     */
    @SerialName("token_type")
    val tokenType: String = "",

    /**
     * The maximum length of the returned token depends on various factors such as token type (by value, and by
     * reference) and the values requested in the token. As an illustration a OAuth 2.0 client which has a name of
     * 20 characters trying to authenticate a user who has the loginID of 20 character, asking for 10 scopes -
     * each of 50 characters will return in 1093 characters in resulting token of token-by-reference type.
     * As tokens are opaque strings, we recommend not to depend on the returned length. If your implementation
     * runs on space constrained - embedded device like setup, we suggest you to abstract the token variations
     * behind a thin wrapper and always provide standard length.
     */
    @SerialName("access_token")
    val accessToken: String = "",

    /**
     * The maximum length of the returned token depends on various factors such as token type (by value, and by
     * reference) and the values requested in the token. As an illustration a OAuth 2.0 client which has a name of
     * 20 characters trying to authenticate a user who has the loginID of 20 character, asking for 10 scopes -
     * each of 50 characters will return in 1093 characters in resulting token of token-by-reference type.
     * As tokens are opaque strings, we recommend not to depend on the returned length. If your implementation
     * runs on space constrained - embedded device like setup, we suggest you to abstract the token variations
     * behind a thin wrapper and always provide standard length.
     */
    @SerialName("refresh_token")
    val refreshToken: String = "",

    /**
     * The remaining time in seconds until this access token is valid.
     */
    @SerialName("expires_in")
    val expiresIn: Long = 0,

    /**
     * A signed token that contains a user's profile information using an IAM certificate.
     * The size of id_token depends on the claims associated with the token.
     */
    @SerialName("id_token")
    val idToken: String = "",

    /**
     * A signed token based on a client's private certificate.
     */
    @SerialName("signed_token")
    val signedToken: String = "",

    /**
     * An identifier for the representation of the issued security token. This identifier will be in
     * the response for grant-type urn:ietf:params:oauth:grant-type:token-exchange.
     */
    @SerialName("issued_token_type")
    val issuedTokenType: String? = null,

    val timestamp: Long = System.currentTimeMillis() / 1000,
) {
    val isExpired: Boolean
        get() = (System.currentTimeMillis() / 1000) - timestamp > expiresIn

    val isValid: Boolean
        get() = accessToken.isNotEmpty() && tokenType.isNotEmpty() && !isExpired

    val isNotValid: Boolean
        get() = !isValid

//    init {
//        require(tokenType.length in 6..256)
//        require(accessToken.length <= 1024)
//        require(refreshToken.length <= 1024)
//        require(expiresIn in 1..9223372036854774000)
//        require(signedToken.length >= 344)
//    }
}
