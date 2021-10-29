/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.oauth2.domain.sdk

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val subject: String,
    val name: String = "",
    val givenName: String = "",
    val familyName: String = "",
    val email: String = "",
    val address: PostalAddress? = null,
    val updatedAtInEpochSeconds: Long = 0,
)
