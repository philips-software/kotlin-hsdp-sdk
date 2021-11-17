/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * Input for triggering user password reset. This may include user's kba challenges and answers based on UI workflow.
 */
@Serializable
data class ResetPassword(
    /**
     * Login id of the user.
     */
    val loginId: String,

    /**
     * User provided questions and answers.
     */
    val challenges: List<UserKbaChallenge>? = null,

    /**
     * Mode through which the reset code has to be delivered.
     * Currently, EMAIL & SMS (case in-sensitive) notification mode is supported.
     */
    val notificationMode: String? = null,
)
