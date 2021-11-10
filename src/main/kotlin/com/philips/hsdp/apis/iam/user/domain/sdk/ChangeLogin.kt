/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.sdk

import com.philips.hsdp.apis.support.UUIDSerializer
import com.thinkinglogic.builder.annotation.Builder
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Builder
data class ChangeLogin(
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    val loginId: String,
)
