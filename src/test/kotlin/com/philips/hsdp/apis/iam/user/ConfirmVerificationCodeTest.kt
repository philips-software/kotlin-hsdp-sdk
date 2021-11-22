/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user

import com.philips.hsdp.apis.iam.user.domain.sdk.Transport
import com.philips.hsdp.apis.iam.user.domain.sdk.VerificationWithCode
import com.philips.hsdp.apis.support.HttpException
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class ConfirmVerificationCodeTest : IamUserTestBase() {

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
                    "diagnostics": "CODE verified successfully"
                }
            ]
        }
        """.trimIndent()
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(response)

        server.enqueue(mockedResponse)

        // When
        iamUser.confirmVerificationCode(verificationCode)

        // Then
        val request = server.takeRequest()
        request.requestUrl?.encodedPath shouldBe "/authorize/identity/User/\$confirm-verification-code"
        request.method shouldBe "POST"
        val headers = request.headers.toMultimap()
        headers shouldContainAll mapOf(
            "authorization" to listOf("Basic $basicAuthenticationValue"),
            "api-version" to listOf("1"),
            "accept" to listOf("application/json; charset=utf-8"),
            "content-type" to listOf("application/json; charset=utf-8"),
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
            iamUser.confirmVerificationCode(verificationCode)
        }

        // Then
        server.takeRequest()
        exception.message shouldBe missingApiVersionHeaderResponse
    }

    private val verificationCode = VerificationWithCode(
        loginId = "loginId",
        transport = Transport.Email,
        code = "12345"
    )

    private val expectedRequestBody = """
    {
        "loginId": "$loginId",
        "factorType": "EMAIL",
        "code": "12345"
    }
    """.trimIndent()
}