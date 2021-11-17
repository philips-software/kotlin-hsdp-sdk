/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
@file:Suppress("unused")

package com.philips.hsdp.apis.iam.user.domain.sdk

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class NotificationMode(val value: String) {
    @SerialName("EMAIL") Email("EMAIL"),
    @SerialName("SMS") Sms("SMS"),
}