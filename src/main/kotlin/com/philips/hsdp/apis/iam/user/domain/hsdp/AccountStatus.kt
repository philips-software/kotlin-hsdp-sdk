/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.hsdp

import com.philips.hsdp.apis.support.ValidationPatterns.iso8601UtcPattern
import kotlinx.serialization.Serializable

/**
 * Profile that holds various account status attributes.
 */
@Serializable
data class AccountStatus(
    /**
     * Date on which a successful login attempt was made by the user.
     */
    val lastLoginTime: String,

    /**
     * Indicates whether the user is challenged with OTP during login flow.
     */
    val mfaStatus: String,

    /**
     * Boolean value indicating whether user email is verified.
     */
    val phoneVerified: Boolean? = null,

    /**
     * Boolean value indicating whether user email is verified.
     */
    val emailVerified: Boolean,

    /**
     * Boolean value indicating whether user password is required to be changed.
     */
    val mustChangePassword: Boolean? = null,

    /**
     * Boolean value indicating whether the user account is in the active state.
     */
    val disabled: Boolean? = null,

    /**
     * Date on which user account was locked due to many login attempts with invalid credentials.
     */
    val accountLockedOn: String? = null,

    /**
     * Date until which the account remains locked.
     */
    val accountLockedUntil: String? = null,

    /**
     * Number of login attempts with the invalid credential. Increments by 1 on each failed login due to
     * the invalid credential. Once the account is locked this count doesn't increment further.
     */
    val numberOfInvalidAttempt: Int,

    /**
     * Date on which last un-successful login attempt was made.
     */
    val lastInvalidAttemptedOn: String? = null
) {
    init {
        require(lastLoginTime.matches(iso8601UtcPattern))
        require(accountLockedOn?.matches(iso8601UtcPattern) ?: true)
        require(accountLockedUntil?.matches(iso8601UtcPattern) ?: true)
        require(lastInvalidAttemptedOn?.matches(iso8601UtcPattern) ?: true)
    }
}
