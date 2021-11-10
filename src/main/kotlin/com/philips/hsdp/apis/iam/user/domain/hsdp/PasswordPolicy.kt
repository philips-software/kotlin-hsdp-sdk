/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.hsdp

import kotlinx.serialization.Serializable

/**
 * The effective password policy.
 */
@Serializable
data class PasswordPolicy(
    /**
     * The number of days after which the user's password expires. The user must set a new password after it expires.
     */
    val expiryPeriodInDays: Int? = null,

    /**
     * Different criteria that decides the strength of user password for an organization.
     */
    val complexity: Complexity? = null,

    /**
     * The number of previous passwords that can not be used as new password.
     */
    val historyCount: Int? = null,

    /**
     * A boolean value indicating if challenges are enabled at organization level.
     * If the value is set to true, challengePolicy attribute is mandatory.
     */
    val challengesEnabled: Boolean? = null,

    /**
     * An object to store information related to KBA.
     */
    val challengePolicy: ChallengePolicy? = null,
)

/**
 * Different criteria that decides the strength of user password for an organization.
 */
@Serializable
data class Complexity(
    /**
     * The minimum number of characters a password can contain.
     */
    val minLength: Int,

    /**
     * The maximum number of characters a password can contain.
     */
    val maxLength: Int? = null,

    /**
     * The minimum number of numerical characters a password can contain.
     */
    val minNumerics: Int? = null,

    /**
     * The minimum number of uppercase characters a password can contain.
     */
    val minUpperCase: Int? = null,

    /**
     * The minimum number of lowercase characters a password can contain.
     */
    val minLowerCase: Int? = null,

    /**
     * The minimum number of special characters a password can contain.
     */
    val minSpecialChars: Int? = null,
)

/**
 * An object to store information related to KBA.
 */
@Serializable
data class ChallengePolicy(
    /**
     * A Multi-valued String attribute that contains one or more default question a user may use
     * when setting their challenge questions.
     */
    val defaultQuestions: List<String>? = null,

    /**
     * An Integer indicating the minimum number of challenge questions a user MUST answer when setting challenge
     * question answers. MinQuestionCount cannot be greater than the number of default questions.
     */
    val minQuestionCount: Int? = null,

    /**
     * An Integer indicating the minimum number of challenge answers a user MUST answer when attempting to reset
     * their password. MinAnswerCount cannot be greater than minQuestionCount.
     */
    val minAnswerCount: Int? = null,

    /**
     * An Integer indicates the maximum number of failed reset password attempts using challenges.
     * Failed attempts beyond this will cause account lockout for 30 minutes.
     */
    val maxIncorrectAttempts: Int? = null,
)
