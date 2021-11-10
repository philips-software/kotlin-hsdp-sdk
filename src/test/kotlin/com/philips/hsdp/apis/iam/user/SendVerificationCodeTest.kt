/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user

import com.philips.hsdp.apis.iam.user.domain.sdk.Transport
import com.philips.hsdp.apis.iam.user.domain.sdk.VerificationCode
import com.philips.hsdp.apis.support.HttpException
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class SendVerificationCodeTest : IamUserTestBase() {

    @Test
    fun `Should not throw when call to HSDP is successful`(): Unit = runBlocking {
        // Given
        val response = """
        {
            "resourceType": "OperationOutcome",
            "issue": [
                {
                    "severity": "information",
                    "code": "informational",
                    "details": {
                        "coding": {},
                        "text": "Verification code has been sent"
                    },
                    "diagnostics": "Verification code has been sent"
                }
            ]
        }
        """.trimIndent()
        val mockedResponse = MockResponse()
            .setResponseCode(202)
            .setBody(response)

        server.enqueue(mockedResponse)

        // When
        iamUser.sendVerificationCode(verificationCode)
        val request = server.takeRequest()

        // Then
        request.requestUrl?.encodedPath shouldBe "/authorize/identity/User/\$send-verification-code"
        request.method shouldBe "POST"
        val headers = request.headers.toMultimap()
        headers shouldContainAll mapOf(
            "authorization" to listOf("Basic $basicAuthenticationValue"),
            "api-version" to listOf("1"),
            "accept" to listOf("application/json; charset=utf-8"),
            "content-type" to listOf("application/json; charset=utf-8"),
            "accept-language" to listOf("en"),
        )
        request.body.readUtf8() shouldEqualJson expectedRequestBody
    }

    @Test
    fun `Should throw when the HSDP reports a status 400 due to a missing Api-Version header`(): Unit = runBlocking {
        // Given
        val mockedResponse = MockResponse()
            .setResponseCode(400)
            .setBody(missingApiVersionHeaderResponse)

        server.enqueue(mockedResponse)

        // When
        val exception = shouldThrowExactly<HttpException> {
            iamUser.sendVerificationCode(verificationCode)
        }
        server.takeRequest()

        // Then
        exception.message shouldBe missingApiVersionHeaderResponse
    }

    private val verificationCode = VerificationCode(
        loginId = "loginId",
        transport = Transport.Email,
        acceptLanguage = "en",
    )

    private val expectedRequestBody = """
    {
        "loginId": "$loginId",
        "factorType": "EMAIL"
    }
    """.trimIndent()
}