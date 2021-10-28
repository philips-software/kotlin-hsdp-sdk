/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.sdk

import com.philips.hsdp.apis.support.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class User(
    val preferredLanguage: String = "",
    val preferredCommunicationChannel: String? = null,
    val emailAddress: String = "",
    val phoneNumber: String? = null,
    @Serializable(with = UUIDSerializer::class)
    val id: UUID? = null,
    val loginId: String = "",
    val name: UserName? = null,
    @Serializable(with = UUIDSerializer::class)
    val managingOrganization: UUID? = null,
    val passwordStatus: PasswordStatus,
    val memberships: List<Membership> = emptyList(),
    val accountStatus: AccountStatus,
    val consentedApps: List<String>,
    val delegations: Delegations? = null,
)
