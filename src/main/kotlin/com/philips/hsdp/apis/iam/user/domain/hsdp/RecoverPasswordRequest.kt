/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.hsdp

import kotlinx.serialization.Serializable

@Serializable
data class RecoverPasswordRequest(
    val resourceType: String,
    val parameter: List<RecoverRequestBody>,
)

@Serializable
data class RecoverRequestBody(
    val name: String,
    val resource: RecoverRequestResource,
)

@Serializable
data class RecoverRequestResource(
    val loginId: String,
)
