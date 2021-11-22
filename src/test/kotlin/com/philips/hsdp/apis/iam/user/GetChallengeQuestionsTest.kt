/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user

import com.philips.hsdp.apis.support.HttpException
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class GetChallengeQuestionsTest : IamUserTestBase() {

    @Test
    fun `Should not throw when unlocking a user is successful`(): Unit = runBlocking {
        // Given
        val response = """
        {
            "challenges": [
                {
                    "challenge": "q1"
                },
                {
                    "challenge": "q2"
                }
            ]
        }
        """.trimIndent()
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(response)

        server.enqueue(mockedResponse)

        // When
        val result = iamUser.getChallengeQuestions(loginId)

        // Then
        val request = server.takeRequest()
        request.requestUrl?.encodedPath shouldBe "/authorize/identity/User/\$kba%3FloginId=$loginId"
        request.method shouldBe "GET"
        val headers = request.headers.toMultimap()
        headers shouldContainAll mapOf(
            "authorization" to listOf("Basic $basicAuthenticationValue"),
            "api-version" to listOf("1"),
            "accept" to listOf("application/json; charset=utf-8"),
        )
        result shouldBe listOf("q1", "q2")
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
            iamUser.getChallengeQuestions(loginId)
        }

        // Then
        server.takeRequest()
        exception.message shouldBe missingApiVersionHeaderResponse
    }
}