/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.support.logging

object PlatformLoggerFactory {
    private var factory: AbstractPlatformLoggerFactory? = null

    @JvmStatic
    fun registerConcreteFactory(factory: AbstractPlatformLoggerFactory) {
        this.factory = factory
    }

    fun create(tag: String, ofClass: Class<*>) =
        factory?.create(tag, ofClass) ?: throw Exception("A concrete logger factory must be registered")
}

interface AbstractPlatformLoggerFactory {
    fun create(tag: String, ofClass: Class<*>): PlatformLogger
}
