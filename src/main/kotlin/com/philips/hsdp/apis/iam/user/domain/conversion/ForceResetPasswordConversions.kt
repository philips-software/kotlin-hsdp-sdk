/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user.domain.conversion

import com.philips.hsdp.apis.iam.user.domain.hsdp.ForceResetPasswordBody
import com.philips.hsdp.apis.iam.user.domain.sdk.ForceResetPassword

fun ForceResetPassword.toForceResetPasswordBody(): ForceResetPasswordBody =
    ForceResetPasswordBody(
        loginId = loginId,
        newPassword = newPassword,
    )
