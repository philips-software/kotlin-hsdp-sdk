/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.sdk

import com.thinkinglogic.builder.annotation.Builder
import kotlinx.serialization.Serializable

@Serializable
@Builder
data class ResendActivation(
    val loginId: String,
    val acceptLanguage: String? = null,
)
