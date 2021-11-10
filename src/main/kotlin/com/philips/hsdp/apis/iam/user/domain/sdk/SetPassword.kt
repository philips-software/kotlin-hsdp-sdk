/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
@file:Suppress("unused")

package com.philips.hsdp.apis.iam.user.domain.sdk

import com.thinkinglogic.builder.annotation.Builder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Builder
data class SetPassword(
    val loginId: String,
    val confirmationCode: String,
    val newPassword: String,
    val context: SetPasswordContext,
)

@Serializable
enum class SetPasswordContext(val value: String) {
    @SerialName("userCreate") UserCreate("userCreate"),
    @SerialName("recoverPassword") RecoverPassword("recoverPassword"),
}
