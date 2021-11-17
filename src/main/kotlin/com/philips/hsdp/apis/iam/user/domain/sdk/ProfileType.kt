/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.sdk

enum class ProfileType(val value: String) {
    Membership("membership"),
    AccountStatus("accountstatus"),
    PasswordStatus("passwordstatus"),
    ConsentedApps("consentedapps"),
    Delegations("delegations"),
    All("all"),
}
