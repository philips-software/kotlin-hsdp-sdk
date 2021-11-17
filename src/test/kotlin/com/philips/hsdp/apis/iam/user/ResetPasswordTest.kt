/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user

import com.philips.hsdp.apis.iam.user.domain.sdk.Challenge
import com.philips.hsdp.apis.iam.user.domain.sdk.NotificationMode
import com.philips.hsdp.apis.iam.user.domain.sdk.ResetPassword
import com.philips.hsdp.apis.support.HttpException
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class ResetPasswordTest : IamUserTestBase() {

    @Test
    fun `Should not throw when call to HSDP is successful`(): Unit = runBlocking {
        // Given
        val mockedResponse = MockResponse()
            .setResponseCode(202)

        server.enqueue(mockedResponse)

        // When
        iamUser.resetPassword(resetPassword)
        val request = server.takeRequest()

        // Then
        request.requestUrl?.encodedPath shouldBe "/authorize/identity/User/\$reset-password"
        request.method shouldBe "POST"
        val headers = request.headers.toMultimap()
        headers shouldContainAll mapOf(
            "api-version" to listOf("1"),
            "content-type" to listOf("application/json; charset=utf-8"),
            "accept" to listOf("application/json; charset=utf-8"),
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
            iamUser.resetPassword(resetPassword)
        }
        server.takeRequest()

        // Then
        exception.message shouldBe missingApiVersionHeaderResponse
    }

    private val resetPassword = ResetPassword(
        loginId = loginId,
        challenges = listOf(
            Challenge(
                question = "q1",
                answer = "a1",
            ),
            Challenge(
                question = "q2",
                answer = "a2",
            ),
        ),
        notificationMode = NotificationMode.Email,
        acceptLanguage = "en",
    )

    private val expectedRequestBody = """
    {
        "loginId": "$loginId",
        "challenges": [
            {
                "challenge": "q1",
                "response": "a1"
            },
            {
                "challenge": "q2",
                "response": "a2"
            }
        ],
        "notificationMode": "EMAIL"
    }
    """.trimIndent()
}