/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.support.logging

interface PlatformLogger {
    fun fatal(message: () -> String)
    fun error(message: () -> String)
    fun warn(message: () -> String)
    fun info(message: () -> String)
    fun debug(message: () -> String)
    fun trace(message: () -> String)
}
