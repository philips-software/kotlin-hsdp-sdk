/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.sdk

import kotlinx.serialization.Serializable

/**
 * Profile that holds various account status attributes.
 */
@Serializable
data class AccountStatus(
    val lastLoginTime: String,
    val mfaStatus: String,
    val phoneVerified: Boolean? = null,
    val emailVerified: Boolean,
    val mustChangePassword: Boolean? = null,
    val disabled: Boolean? = null,
    val accountLockedOn: String? = null,
    val accountLockedUntil: String? = null,
    val numberOfInvalidAttempt: Int,
    val lastInvalidAttemptedOn: String? = null
)
