/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.oauth2.domain.sdk

import com.philips.hsdp.apis.iam.oauth2.domain.hsdp.Actor
import com.philips.hsdp.apis.iam.oauth2.domain.hsdp.Organizations
import com.philips.hsdp.apis.support.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class TokenMetadata(
    val isActive: Boolean = false,
    val scopes: String = "",
    val clientId: String = "",
    val userName: String = "",
    val tokenType: String = "",
    val expirationTimeInEpochSeconds: Long = 0,
    @Serializable(with = UUIDSerializer::class)
    val subject: UUID? = null,
    val issuer: String = "",
    val identityType: String = "",
    val deviceType: String = "",
    val organizations: Organizations? = null,
    val tokenTypeHint: String = "",
    val clientOrganizationId: String = "",
    val actor: Actor? = null,
)
