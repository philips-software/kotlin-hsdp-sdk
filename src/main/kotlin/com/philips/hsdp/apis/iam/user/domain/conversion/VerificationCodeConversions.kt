/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.conversion

import com.philips.hsdp.apis.iam.user.domain.hsdp.ConfirmVerificationCodeBody
import com.philips.hsdp.apis.iam.user.domain.hsdp.SendVerificationCodeBody
import com.philips.hsdp.apis.iam.user.domain.sdk.VerificationCode
import com.philips.hsdp.apis.iam.user.domain.sdk.VerificationWithCode

fun VerificationCode.toSendVerificationCodeBody(): SendVerificationCodeBody =
    SendVerificationCodeBody(
        loginId = loginId,
        factorType = transport.value,
    )

fun VerificationWithCode.toConfirmVerificationCodeBody(): ConfirmVerificationCodeBody =
    ConfirmVerificationCodeBody(
        loginId = loginId,
        factorType = transport.value,
        code = code,
    )
