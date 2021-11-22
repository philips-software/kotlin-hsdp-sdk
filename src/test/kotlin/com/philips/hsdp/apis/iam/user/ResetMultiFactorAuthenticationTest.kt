/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.iam.user

import com.philips.hsdp.apis.support.HttpException
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

class ResetMultiFactorAuthenticationTest : IamUserTestBase() {

    @Test
    fun `Should not throw when call to HSDP is successful`(): Unit = runBlocking {
        // Given
        val mockedResponse = MockResponse()
            .setResponseCode(204)

        server.enqueue(mockedResponse)

        // When
        iamUser.resetMultiFactorAuthentication(userId)

        // Then
        val request = server.takeRequest()
        request.requestUrl?.encodedPath shouldBe "/authorize/identity/User/$userId/\$mfa-reset"
        request.method shouldBe "POST"
        val headers = request.headers.toMultimap()
        headers shouldContainAll mapOf(
            "authorization" to listOf("Bearer ${httpClient.token.accessToken}"),
            "api-version" to listOf("2"),
            "content-type" to listOf("application/json; charset=utf-8"),
            "accept" to listOf("application/json; charset=utf-8"),
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
            iamUser.resetMultiFactorAuthentication(userId)
        }

        // Then
        server.takeRequest()
        exception.message shouldBe missingApiVersionHeaderResponse
    }

    private val expectedRequestBody = """
    {
        "action": "reset"
    }
    """.trimIndent()
}