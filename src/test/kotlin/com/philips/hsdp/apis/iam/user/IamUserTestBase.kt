/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user

import com.philips.hsdp.apis.iam.oauth2.domain.sdk.Token
import com.philips.hsdp.apis.support.HttpClient
import com.philips.hsdp.apis.support.TokenRefresher
import com.philips.hsdp.apis.support.logging.MockLoggerFactory
import com.philips.hsdp.apis.support.logging.PlatformLoggerFactory
import io.mockk.every
import io.mockk.mockk
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.TestInstance
import java.time.Duration
import java.util.Base64
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class IamUserTestBase {

    @AfterAll
    fun afterAll() {
        server.shutdown()
    }

    init {
        PlatformLoggerFactory.registerConcreteFactory(MockLoggerFactory)
    }

    protected val server = MockWebServer().apply {
        start()
    }
    private val tokenRefresherMock = mockk<TokenRefresher>().apply {
        every { token } returns Token(accessToken = "22a34a6e-214c-4e3e-b85f-b4bbd1448613")
    }
    val httpClient = HttpClient(callTimeout = Duration.ofMillis(300)).apply {
        tokenRefresher = tokenRefresherMock
    }
    protected val iamUser = IamUser(
        idmUrl = server.url("").toString(),
        httpClient = httpClient,
        clientId = "clientId",
        clientSecret = "clientSecret",
        sharedKey = "sharedKey",
        secretKey = "secretKey",
        signingPrefix = "signingPrefix"
    )
    protected val basicAuthenticationValue: String =
        Base64.getEncoder().encodeToString("clientId:clientSecret".toByteArray())

    protected val managingOrganization: UUID = UUID.randomUUID()
    protected val userId: UUID = UUID.randomUUID()
    protected val loginId = "loginId"

    protected val missingApiVersionHeaderResponse = """
    {
        "issue": [
            {
                "severity": "error",
                "code": "invalid",
                "details": {
                    "coding": {
                        "system": "extension",
                        "code": "10102"
                    },
                    "text": "Invalid header value."
                },
                "diagnostics": "API-Version' specified is not supported."
            }
        ],
        "resourceType": "OperationOutcome"
    }
    """.trimIndent()
}