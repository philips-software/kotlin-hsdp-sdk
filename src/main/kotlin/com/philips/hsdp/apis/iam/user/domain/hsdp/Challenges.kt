/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.hsdp

import kotlinx.serialization.Serializable

@Serializable
data class Challenges(val challenges: List<ChallengeQuestion>)

@Serializable
data class ChallengeQuestion(val challenge: String)
