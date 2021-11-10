/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.hsdp

import kotlinx.serialization.Serializable

@Serializable
data class SetPasswordRequest(
    val resourceType: String,
    val parameter: List<SetPasswordRequestBody>,
)

@Serializable
data class SetPasswordRequestBody(
    val name: String,
    val resource: SetPasswordResource,
)

@Serializable
data class SetPasswordResource(
    val loginId: String,
    val confirmationCode: String,
    val newPassword: String,
    val context: String,
)
