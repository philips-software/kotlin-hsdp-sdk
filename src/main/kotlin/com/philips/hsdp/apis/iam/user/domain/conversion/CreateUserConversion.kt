/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.conversion

import com.philips.hsdp.apis.iam.user.domain.hsdp.ContactPoint
import com.philips.hsdp.apis.iam.user.domain.hsdp.ContactType
import com.philips.hsdp.apis.iam.user.domain.hsdp.HumanName
import com.philips.hsdp.apis.iam.user.domain.hsdp.Person
import com.philips.hsdp.apis.iam.user.domain.sdk.Address
import com.philips.hsdp.apis.iam.user.domain.sdk.CreateUserAsAdmin
import com.philips.hsdp.apis.iam.user.domain.sdk.SelfCreateUser
import com.philips.hsdp.apis.iam.user.domain.sdk.UserName
import com.philips.hsdp.apis.iam.user.domain.hsdp.Address as HsdpAddress
import com.philips.hsdp.apis.iam.user.domain.hsdp.AddressUse as HsdpAddressUse

fun CreateUserAsAdmin.toPerson(): Person =
    Person(
        resourceType = "Person",
        loginId = loginId,
        password = password,
        name = name.toHumanName(),
        telecom = listOfNotNull(
            email?.let { ContactPoint(ContactType.Email, it) },
            mobile?.let { ContactPoint(ContactType.Mobile, it) },
        ),
        address = addresses?.map { it.toHsdpAddress() },
        managingOrganization = managingOrganization,
        preferredLanguage = preferredLanguage,
        preferredCommunicationChannel = preferredCommunicationChannel,
    )

fun SelfCreateUser.toPerson(): Person =
    Person(
        resourceType = "Person",
        loginId = loginId,
        password = password,
        name = name.toHumanName(),
        telecom = listOfNotNull(
            email?.let { ContactPoint(ContactType.Email, it) },
            mobile?.let { ContactPoint(ContactType.Mobile, it) },
        ),
        address = addresses?.map { it.toHsdpAddress() },
        isAgeValidated = if (isAgeValidated) "true" else "false",
        preferredLanguage = preferredLanguage,
        preferredCommunicationChannel = preferredCommunicationChannel,
    )

fun UserName.toHumanName(): HumanName =
    HumanName(
        family = family,
        given = given,
    )

fun Address.toHsdpAddress(): HsdpAddress =
    HsdpAddress(
        use = use?.let { HsdpAddressUse.valueOf( it.name) },
        text = text,
        line = lines,
        city = city,
        state = state,
        postalCode = postalCode,
        country = country,
    )
