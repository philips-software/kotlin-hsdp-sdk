/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.conversion

import com.philips.hsdp.apis.iam.user.domain.hsdp.ChangePasswordRequest
import com.philips.hsdp.apis.iam.user.domain.hsdp.ChangePasswordRequestBody
import com.philips.hsdp.apis.iam.user.domain.hsdp.ChangePasswordResource
import com.philips.hsdp.apis.iam.user.domain.sdk.ChangePassword

fun ChangePassword.toChangePasswordRequest(): ChangePasswordRequest =
    ChangePasswordRequest(
        resourceType = "Parameters",
        parameter = listOf(
            ChangePasswordRequestBody(
                name = "changePassword",
                resource = ChangePasswordResource(
                    loginId = loginId,
                    oldPassword = oldPassword,
                    newPassword = newPassword,
                )
            )
        )
    )
