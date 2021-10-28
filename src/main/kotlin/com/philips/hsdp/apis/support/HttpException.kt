/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.support

import kotlinx.serialization.Serializable
import java.io.IOException

/**
 * Exception that is thrown when handling the request/response to/from HSDP TDR API.
 */
@Serializable
class HttpException(val code: Int, override val message: String) : IOException(message)
