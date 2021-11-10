/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.hsdp

import com.philips.hsdp.apis.support.ValidationPatterns.iso8601UtcPattern
import kotlinx.serialization.Serializable

/**
 * Profile that holds user password activities like the last password changed date and password expiry date.
 */
@Serializable
data class PasswordStatus(
    /**
     * Date on which the current password is going to expire.
     */
    val passwordExpiresOn: String? = null,

    /**
     * Date on which current password was set.
     */
    val passwordChangedOn: String? = null,
) {
    init {
        require(passwordExpiresOn?.matches(iso8601UtcPattern) ?: true)
        require(passwordChangedOn?.matches(iso8601UtcPattern) ?: true)
    }
}
