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
data class VerificationCode(
    val loginId: String,
    val transport: Transport,
    val acceptLanguage: String? = null,
)

@Serializable
@Builder
data class VerificationWithCode(
    val loginId: String,
    val transport: Transport,
    val code: String,
)

@Serializable
enum class Transport(val value: String) {
    @SerialName("EMAIL") Email("EMAIL"),
    @SerialName("SMS") Sms("SMS"),
}
