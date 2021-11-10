/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.conversion

import com.philips.hsdp.apis.iam.user.domain.hsdp.UserKbaChallenge
import com.philips.hsdp.apis.iam.user.domain.sdk.Challenge
import com.philips.hsdp.apis.iam.user.domain.sdk.ResetPassword
import com.philips.hsdp.apis.iam.user.domain.hsdp.ResetPassword as HsdpResetPassword

fun ResetPassword.toHsdpResetPassword(): HsdpResetPassword =
    HsdpResetPassword(
        loginId = loginId,
        challenges = challenges?.map { it.toHsdpChallenge() },
        notificationMode = notificationMode?.value,
    )

fun Challenge.toHsdpChallenge(): UserKbaChallenge =
    UserKbaChallenge(
        challenge = question,
        response = answer,
    )
