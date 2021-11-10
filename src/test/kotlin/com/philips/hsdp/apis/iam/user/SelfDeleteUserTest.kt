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

class SelfDeleteUserTest : IamUserTestBase() {

    @Test
    fun `Should not throw when call to HSDP is successful`(): Unit = runBlocking {
        // Given
        val mockedResponse = MockResponse()
            .setResponseCode(204)

        server.enqueue(mockedResponse)

        // When
        iamUser.selfDeleteUser(userId)
        val request = server.takeRequest()

        // Then
        request.requestUrl?.encodedPath shouldBe "/authorize/identity/User/$userId"
        request.method shouldBe "DELETE"
        request.headers.toMultimap() shouldContainAll mapOf(
            "api-version" to listOf("2"),
            "accept" to listOf("application/json; charset=utf-8"),
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
            iamUser.selfDeleteUser(userId)
        }
        server.takeRequest()

        // Then
        exception.message shouldBe missingApiVersionHeaderResponse
    }
}
