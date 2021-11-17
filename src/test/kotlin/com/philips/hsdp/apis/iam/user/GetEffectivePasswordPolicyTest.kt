/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user

import com.philips.hsdp.apis.iam.user.domain.hsdp.ChallengePolicy
import com.philips.hsdp.apis.iam.user.domain.hsdp.Complexity
import com.philips.hsdp.apis.iam.user.domain.hsdp.PasswordPolicy
import com.philips.hsdp.apis.support.HttpException
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class GetEffectivePasswordPolicyTest : IamUserTestBase() {

    @Test
    fun `Should not throw when call to HSDP is successful`(): Unit = runBlocking {
        // Given
        val response = """
        {
            "expiryPeriodInDays": 100,
            "complexity": {
                "minLength": 14,
                "maxLength": 20,
                "minNumerics": 2,
                "minUpperCase": 3,
                "minLowerCase": 4,
                "minSpecialChars": 5
             },
            "historyCount": 6,
            "challengesEnabled": true,
            "challengePolicy": {
                "defaultQuestions": ["q1"],
                "minQuestionCount": 2,
                "minAnswerCount": 3,
                "maxIncorrectAttempts": 4
            }
        }
        """.trimIndent()
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(response)

        server.enqueue(mockedResponse)

        // When
        val result = iamUser.getEffectivePasswordPolicy(userId)
        val request = server.takeRequest()

        // Then
        request.requestUrl?.encodedPath shouldBe "/authorize/identity/User/$userId/\$password-policy"
        request.method shouldBe "GET"
        val headers = request.headers.toMultimap()
        headers shouldContainAll mapOf(
            "authorization" to listOf("Bearer ${httpClient.token.accessToken}"),
            "api-version" to listOf("1"),
            "accept" to listOf("application/json; charset=utf-8"),
        )
        result shouldBe PasswordPolicy(
            expiryPeriodInDays = 100,
            complexity = Complexity(
                minLength = 14,
                maxLength = 20,
                minNumerics = 2,
                minUpperCase = 3,
                minLowerCase = 4,
                minSpecialChars = 5,
            ),
            historyCount = 6,
            challengesEnabled = true,
            challengePolicy = ChallengePolicy(
                defaultQuestions = listOf("q1"),
                minQuestionCount = 2,
                minAnswerCount = 3,
                maxIncorrectAttempts = 4,
            )
        )
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
            iamUser.getEffectivePasswordPolicy(userId)
        }
        server.takeRequest()

        // Then
        exception.message shouldBe missingApiVersionHeaderResponse
    }
}