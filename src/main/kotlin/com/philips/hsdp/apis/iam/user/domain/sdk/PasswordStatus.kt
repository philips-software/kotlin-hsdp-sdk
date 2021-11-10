/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.sdk

import kotlinx.serialization.Serializable

/**
 * Profile that holds user password activities like the last password changed date and password expiry date.
 */
@Serializable
data class PasswordStatus(
    val passwordExpiresOn: String? = null,
    val passwordChangedOn: String? = null,
)
