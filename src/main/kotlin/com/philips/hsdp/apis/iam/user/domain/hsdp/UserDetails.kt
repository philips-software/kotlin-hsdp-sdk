/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.hsdp

import com.philips.hsdp.apis.support.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * User membership details with user meta information.
 */
@Serializable
data class UserDetails(
    /**
     * International language code as user choice of language of communication.
     * Example: en
     */
    val preferredLanguage: String = "",

    /**
     * Preferred communication channel. Email and SMS are supported channels. Email is the default channel.
     */
    val preferredCommunicationChannel: String? = null,

    /**
     * E-mail address that this account is registered with.
     */
    val emailAddress: String = "",

    /**
     * Phone number that this account is registered with.
     */
    val phoneNumber: String? = null,

    /**
     * Unique ID of the user.
     */
    @Serializable(with = UUIDSerializer::class)
    val id: UUID? = null,

    /**
     * Unique login ID of the user.
     * Example: jhondoe
     */
    val loginId: String = "",

    /**
     * A name of a human with text, parts and usage information.
     */
    val name: HumanName? = null,

    /**
     * Managing organization of the user.
     */
    @Serializable(with = UUIDSerializer::class)
    val managingOrganization: UUID? = null,

    /**
     * Profile that holds user password activities like the last password changed date and password expiry date.
     */
    val passwordStatus: PasswordStatus,

    /**
     * Contains list of groups names where the user is a member and list of role names that the user is having
     * in a given target organization.
     */
    val memberships: List<Membership> = emptyList(),

    /**
     * Profile that holds various account status attributes.
     */
    val accountStatus: AccountStatus,

    /**
     * Contains list of persisted consent to share information with. When no persisted consent found, then this
     * profile contains '(default, default, default)' string indicating default set of attributes are shared
     * with all application clients.
     *
     * Each item contains application name (OAuth 2.0 client id) and user information shared with the application
     * as space separated values.
     *
     * Example:
     * [ "PrintApp cn sn email", "default default default", "SchedulePro email idtoken" ]
     */
    val consentedApps: List<String>,

    /**
     * Delegations associated with user.
     */
    val delegations: Delegations? = null
)
