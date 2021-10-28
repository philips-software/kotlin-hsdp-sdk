/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.oauth2.domain.sdk

import kotlinx.serialization.Serializable

/**
 * Token information for a logged in identity.
 */
@Serializable
data class Token(
    val scopes: String = "",
    val tokenType: String= "",
    val accessToken: String = "",
    val refreshToken: String = "",
    val expiresIn: Long = 0,
    val idToken: String = "",
    val signedToken: String = "",
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