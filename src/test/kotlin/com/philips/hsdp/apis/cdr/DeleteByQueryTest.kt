/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr

import com.philips.hsdp.apis.cdr.domain.sdk.DeleteByQueryRequest
import com.philips.hsdp.apis.cdr.domain.sdk.DeleteResponse
import com.philips.hsdp.apis.cdr.domain.sdk.QueryParameter
import com.philips.hsdp.apis.support.HttpException
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DeleteByQueryTest : CdrTestBase() {

    private val basicRequest = DeleteByQueryRequest("Patient", listOf(QueryParameter("name", "value")))
    private val mockSuccessResponse = MockResponse()
        .setResponseCode(204)
        .setHeader("ETag", """W/"abc"""")

    @Test
    fun `Should return the response when CDR returns a 204 with the specified headers`(): Unit = runBlocking {
        // Given
        server.enqueue(mockSuccessResponse)

        // When
        val result = cdr.delete(basicRequest)
        val request = server.takeRequest()

        // Then
        request.requestUrl?.encodedPath shouldBe "/Patient"
        request.requestUrl?.encodedQuery shouldBe "name=value"
        request.method shouldBe "DELETE"
        request.headers.toMultimap() shouldContainAll mapOf(
            "authorization" to listOf("Bearer ${httpClient.token.accessToken}"),
            "api-version" to listOf("1"),
            "accept" to listOf("application/fhir+json; charset=UTF-8; fhirVersion=3.0"),
        )
        result shouldBe DeleteResponse(204, "", "abc")
    }

    @Test
    fun `Should throw when CDR returns a 500`(): Unit = runBlocking {
        // Given
        val mockedResponse = MockResponse()
            .setResponseCode(500)
            .setBody("""{"some":"error"}""")
        server.enqueue(mockedResponse)

        // When
        val exception = shouldThrowExactly<HttpException> {
            cdr.delete(basicRequest)
        }
        server.takeRequest()

        // Then
        exception.code shouldBe 500
        exception.message shouldBe """{"some":"error"}"""
    }

    @Test
    fun `Should not throw when CDR does not include an ETag header`(): Unit = runBlocking {
        // Given
        val mockedResponse = MockResponse()
            .setResponseCode(204)
        server.enqueue(mockedResponse)

        // When/Then
        shouldNotThrowAny {
            val result = cdr.delete(basicRequest)

            result.versionId should beNull()
        }
        server.takeRequest()
    }
}