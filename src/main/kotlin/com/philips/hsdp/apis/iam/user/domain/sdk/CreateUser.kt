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
data class CreateUserAsAdmin(
    val loginId: String,
    val password: String? = null,
    val name: UserName,
    val mobile: String? =  null,
    val email: String? = null,
    val addresses: List<Address>? = null,
    val preferredLanguage: String? = null,
    val preferredCommunicationChannel: String? = null,
    @Serializable(with = UUIDSerializer::class)
    val managingOrganization: UUID,
)

@Serializable
@Builder
data class SelfCreateUser(
    val loginId: String,
    val password: String? = null,
    val name: UserName,
    val mobile: String? =  null,
    val email: String? = null,
    val addresses: List<Address>? = null,
    val preferredLanguage: String? = null,
    val preferredCommunicationChannel: String? = null,
    val isAgeValidated: Boolean,
)
