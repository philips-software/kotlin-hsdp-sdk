/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.conversion

import com.philips.hsdp.apis.iam.user.domain.hsdp.SetPasswordRequest
import com.philips.hsdp.apis.iam.user.domain.hsdp.SetPasswordRequestBody
import com.philips.hsdp.apis.iam.user.domain.hsdp.SetPasswordResource
import com.philips.hsdp.apis.iam.user.domain.sdk.SetPassword

fun SetPassword.toSetPasswordRequest(): SetPasswordRequest =
    SetPasswordRequest(
        resourceType = "Parameters",
        parameter = listOf(
            SetPasswordRequestBody(
                name = "setPassword",
                resource = SetPasswordResource(
                    loginId = loginId,
                    confirmationCode = confirmationCode,
                    newPassword = newPassword,
                    context = context.value,
                ),
            ),
        ),
    )
