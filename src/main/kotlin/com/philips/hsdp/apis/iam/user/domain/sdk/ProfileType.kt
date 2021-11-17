package com.philips.hsdp.apis.iam.user.domain.sdk

enum class ProfileType(val value: String) {
    Membership("membership"),
    AccountStatus("accountstatus"),
    PasswordStatus("passwordstatus"),
    ConsentedApps("consentedapps"),
    Delegations("delegations"),
    All("all"),
}
