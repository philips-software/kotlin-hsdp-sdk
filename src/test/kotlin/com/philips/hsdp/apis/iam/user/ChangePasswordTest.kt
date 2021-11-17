/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user

import com.philips.hsdp.apis.iam.user.domain.sdk.ChangePassword
import com.philips.hsdp.apis.support.HttpException
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.maps.shouldHaveKey
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class ChangePasswordTest : IamUserTestBase() {

    @Test
    fun `Should not throw when call to HSDP is successful`(): Unit = runBlocking {
        // Given
        val response = """
        {
            "resourceType": "OperationOutcome",
            "issue": []
        }
        """.trimIndent()
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(response)

        server.enqueue(mockedResponse)

        // When
        iamUser.changePassword(changePassword)
        val request = server.takeRequest()

        // Then
        request.requestUrl?.encodedPath shouldBe "/authorize/identity/User/\$change-password"
        request.method shouldBe "POST"
        val headers = request.headers.toMultimap()
        headers shouldContainAll mapOf(
            "api-version" to listOf("1"),
            "content-type" to listOf("application/json; charset=utf-8"),
            "accept" to listOf("application/json; charset=utf-8"),
            "accept-language" to listOf("en"),
        )
        headers shouldHaveKey "hsdp-api-signature"
        headers shouldHaveKey "signeddate"
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
            iamUser.changePassword(changePassword)
        }
        server.takeRequest()

        // Then
        exception.message shouldBe missingApiVersionHeaderResponse
    }

    private val changePassword = ChangePassword(
        loginId = loginId,
        oldPassword = "oldPassword",
        newPassword = "newPassword",
        acceptLanguage = "en",
    )

    private val expectedRequestBody = """
    {
        "resourceType": "Parameters",
        "parameter": [
            {
                "name": "changePassword",
                "resource": {
                    "loginId": "$loginId",
                    "oldPassword": "oldPassword",
                    "newPassword": "newPassword"
                }
            }
        ]
    }
    """.trimIndent()
}