/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.oauth2.domain.conversion

import com.philips.hsdp.apis.iam.oauth2.domain.hsdp.TokenResponse
import com.philips.hsdp.apis.iam.oauth2.domain.sdk.Token

fun TokenResponse.toToken(): Token = Token(
    scopes = scopes,
    tokenType = tokenType,
    accessToken = accessToken,
    refreshToken = refreshToken,
    expiresIn = expiresIn,
    idToken = idToken,
    signedToken = signedToken,
    issuedTokenType = issuedTokenType,
)
