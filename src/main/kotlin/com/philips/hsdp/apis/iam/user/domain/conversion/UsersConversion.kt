/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.conversion

import com.philips.hsdp.apis.iam.user.domain.hsdp.HumanName
import com.philips.hsdp.apis.iam.user.domain.hsdp.UserDetails
import com.philips.hsdp.apis.iam.user.domain.hsdp.Users
import com.philips.hsdp.apis.iam.user.domain.hsdp.AccountStatus as HsdpAccountStatus
import com.philips.hsdp.apis.iam.user.domain.hsdp.Delegations as HsdpDelegations
import com.philips.hsdp.apis.iam.user.domain.hsdp.GrantedDelegation as HsdpGrantedDelegation
import com.philips.hsdp.apis.iam.user.domain.hsdp.Membership as HsdpMembership
import com.philips.hsdp.apis.iam.user.domain.hsdp.PasswordStatus as HsdpPasswordStatus
import com.philips.hsdp.apis.iam.user.domain.hsdp.ReceivedDelegation as HsdpReceivedDelegation
import com.philips.hsdp.apis.iam.user.domain.sdk.*

fun Users.toUserList(): List<User> = entry.map {
    it.toUser()
}

fun UserDetails.toUser(): User = User(
    preferredLanguage = preferredLanguage,
    preferredCommunicationChannel = preferredCommunicationChannel,
    emailAddress = emailAddress,
    phoneNumber = phoneNumber,
    id = id,
    loginId = loginId,
    name = name?.toUserName(),
    managingOrganization = managingOrganization,
    passwordStatus = passwordStatus.toPasswordStatus(),
    memberships = memberships.map { it.toMembership() },
    accountStatus = accountStatus.toAccountStatus(),
    consentedApps = consentedApps,
    delegations = delegations?.toDelegations(),
)

fun HumanName.toUserName(): UserName = UserName(
    family = family,
    given = given,
)

fun HsdpPasswordStatus.toPasswordStatus(): PasswordStatus = PasswordStatus(
    passwordExpiresOn = passwordExpiresOn,
    passwordChangedOn = passwordChangedOn,
)

fun HsdpMembership.toMembership(): Membership = Membership(
    organizationId = organizationId,
    organizationName = organizationName,
    roles = roles,
    groups = groups,
)

fun HsdpAccountStatus.toAccountStatus(): AccountStatus = AccountStatus(
    lastLoginTime = lastLoginTime,
    mfaStatus = mfaStatus,
    phoneVerified = phoneVerified,
    emailVerified = emailVerified,
    mustChangePassword = mustChangePassword,
    disabled = disabled,
    accountLockedOn = accountLockedOn,
    accountLockedUntil = accountLockedUntil,
    numberOfInvalidAttempt = numberOfInvalidAttempt,
    lastInvalidAttemptedOn = lastInvalidAttemptedOn,
)

fun HsdpDelegations.toDelegations(): Delegations = Delegations(
    granted = granted.map { it.toGrantedDelegation() },
    received = received.map { it.toReceivedDelegation() },
)

fun HsdpGrantedDelegation.toGrantedDelegation(): GrantedDelegation = GrantedDelegation(
    delegateeId = delegateeId,
    validFrom = validFrom,
    validUntil = validUntil,
)
fun HsdpReceivedDelegation.toReceivedDelegation(): ReceivedDelegation = ReceivedDelegation(
    delegatorId = delegatorId,
    validFrom = validFrom,
    validUntil = validUntil,
)

