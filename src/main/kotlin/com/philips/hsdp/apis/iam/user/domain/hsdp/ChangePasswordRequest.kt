/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.hsdp

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(
    val resourceType: String? = null,
    val parameter: List<ChangePasswordRequestBody>? = null,
)

@Serializable
data class ChangePasswordRequestBody(
    val name: String,
    val resource: ChangePasswordResource,
)

@Serializable
data class ChangePasswordResource(
    val loginId: String,
    val oldPassword: String,
    val newPassword: String,
)

