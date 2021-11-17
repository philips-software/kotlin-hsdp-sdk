/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.conversion

import com.philips.hsdp.apis.iam.user.domain.hsdp.UserKbaRequest
import com.philips.hsdp.apis.iam.user.domain.sdk.UserChallenges

fun UserChallenges.toUserKbaRequest(): UserKbaRequest =
    UserKbaRequest(
        challenges = challenges.map { it.toHsdpChallenge() }
    )