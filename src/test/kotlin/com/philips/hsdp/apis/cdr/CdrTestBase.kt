/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr

import com.philips.hsdp.apis.iam.oauth2.domain.sdk.Token
import com.philips.hsdp.apis.support.HttpClient
import com.philips.hsdp.apis.support.TokenRefresher
import com.philips.hsdp.apis.support.logging.MockLoggerFactory
import com.philips.hsdp.apis.support.logging.PlatformLoggerFactory
import io.mockk.every
import io.mockk.mockk
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import java.time.Duration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class CdrTestBase {
    init {
        PlatformLoggerFactory.registerConcreteFactory(MockLoggerFactory)
    }

    protected val server = MockWebServer().apply {
        start()
    }
    private val tokenRefresherMock = mockk<TokenRefresher>()
    val httpClient = HttpClient(callTimeout = Duration.ofMillis(200)).apply {
        tokenRefresher = tokenRefresherMock
    }
    protected val cdr = CDR(
        cdrUrl = server.url("").toString(),
        fhirVersion = "3.0",
        mediaType = "application/fhir+json; charset=UTF-8",
        httpClient = httpClient
    )

    @AfterAll
    fun afterAll() {
        server.shutdown()
    }

    @BeforeEach
    fun setup() {
        // Next stub cannot be put in the apply-block above, as there is a test that modifies
        // the returned token to have an empty accessToken, which would then lead to other tests
        // failing on an empty token.
        every { tokenRefresherMock.token } returns Token(accessToken = "22a34a6e-214c-4e3e-b85f-b4bbd1448613")
    }
}
