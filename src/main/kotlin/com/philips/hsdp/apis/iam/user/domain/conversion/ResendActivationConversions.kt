/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.conversion

import com.philips.hsdp.apis.iam.user.domain.hsdp.RecoverPasswordRequest
import com.philips.hsdp.apis.iam.user.domain.hsdp.RecoverRequestBody
import com.philips.hsdp.apis.iam.user.domain.hsdp.RecoverRequestResource
import com.philips.hsdp.apis.iam.user.domain.sdk.ResendActivation

fun ResendActivation.toRecoverPasswordRequest(): RecoverPasswordRequest =
    RecoverPasswordRequest(
        resourceType = "Parameters",
        parameter = listOf(
            RecoverRequestBody(
                name = "resendOTP",
                resource = RecoverRequestResource(
                    loginId = loginId
                )
            )
        )
    )
