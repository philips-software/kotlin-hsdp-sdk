/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.oauth2.domain.conversion

import com.philips.hsdp.apis.iam.oauth2.domain.hsdp.AddressClaim
import com.philips.hsdp.apis.iam.oauth2.domain.hsdp.UserInfoResponse
import com.philips.hsdp.apis.iam.oauth2.domain.sdk.PostalAddress
import com.philips.hsdp.apis.iam.oauth2.domain.sdk.UserInfo

fun UserInfoResponse.toUserInfo() = UserInfo(
    subject = sub,
    name = name,
    givenName = givenName,
    familyName = familyName,
    email = email,
    address = address?.toPostalAddress(),
    updatedAtInEpochSeconds = updatedAt,
)

fun AddressClaim.toPostalAddress() = PostalAddress(
    formatted = formatted,
    streetAddress = streetAddress,
    postalCode = postalCode,
)
