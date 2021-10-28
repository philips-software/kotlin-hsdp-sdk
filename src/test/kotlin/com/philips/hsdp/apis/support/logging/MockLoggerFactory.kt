/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.support.logging

import io.mockk.mockk

object MockLoggerFactory : AbstractPlatformLoggerFactory {
    override fun create(tag: String, ofClass: Class<*>): PlatformLogger = mockk(relaxed = true)
}
