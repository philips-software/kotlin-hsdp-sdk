/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.support

import com.philips.hsdp.apis.iam.oauth2.domain.sdk.Token

/**
 * Interface that provides the "user" with a token and a means to refresh it if needed.
 *
 * This interface is implemented by [IamOAuth2][com.philips.hsdp.apis.iam.oauth2.IamOAuth2].
 */
interface TokenRefresher {
    val token: Token
    suspend fun refreshToken(): Token
}
