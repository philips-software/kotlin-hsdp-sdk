/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.hsdp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Error or warning code.
 */
@Serializable
enum class IssueCode {
    @SerialName("invalid") Invalid,
    @SerialName("structure") Structure,
    @SerialName("required") Required,
    @SerialName("value") Value,
    @SerialName("invariant") Invariant,
    @SerialName("security") Security,
    @SerialName("login") Login,
    @SerialName("unknown") Unknown,
    @SerialName("expired") Expired,
    @SerialName("forbidden") Forbidden,
    @SerialName("suppressed") Suppressed,
    @SerialName("processing") Processing,
    @SerialName("not-supported") NotSupported,
    @SerialName("duplicate") Duplicate,
    @SerialName("not-found") NotFound,
    @SerialName("too-long") TooLong,
    @SerialName("code-invalid") CodeInvalid,
    @SerialName("extension") Extension,
    @SerialName("too-costly") TooCostly,
    @SerialName("business-rule") BusinessRule,
    @SerialName("conflict") Conflict,
    @SerialName("incomplete") Incomplete,
    @SerialName("transient") Transient,
    @SerialName("lock-error") LockError,
    @SerialName("no-store") NoStore,
    @SerialName("exception") Exception,
    @SerialName("timeout") Timeout,
    @SerialName("throttled") Throttled,
    @SerialName("informational") Informational,
}
