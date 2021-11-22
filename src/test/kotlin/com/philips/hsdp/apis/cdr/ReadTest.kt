/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr

import com.philips.hsdp.apis.cdr.domain.sdk.FormatParameter
import com.philips.hsdp.apis.cdr.domain.sdk.ReadRequest
import com.philips.hsdp.apis.cdr.domain.sdk.ReadResponse
import com.philips.hsdp.apis.support.HttpException
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class ReadTest : CdrTestBase() {

    private val basicRequest = ReadRequest("Patient", "id")
    private val mockSuccessResponse = MockResponse()
        .setResponseCode(200)
        .setHeader("ETag", """W/"abc"""")
        .setHeader("Last-Modified", "Tue, 21 Sep 2021 17:11:39 GMT")
        .setBody("""{"some":"value"}""")


    @Test
    fun `Should return the response when CDR returns a 200 with the specified headers`(): Unit = runBlocking {
        // Given
        server.enqueue(mockSuccessResponse)

        // When
        val result = cdr.read(basicRequest)

        // Then
        val request = server.takeRequest()
        request.requestUrl?.encodedPath shouldBe "/Patient/id"
        request.requestUrl?.encodedQuery should beNull()
        request.method shouldBe "GET"
        request.headers.toMultimap() shouldContainAll mapOf(
            "authorization" to listOf("Bearer ${httpClient.token.accessToken}"),
            "api-version" to listOf("1"),
            "accept" to listOf("application/fhir+json; charset=UTF-8; fhirVersion=3.0"),
        )
        result shouldBe ReadResponse(200, """{"some":"value"}""", "abc", "2021-09-21T17:11:39Z")
    }

    @TestFactory
    fun `FormatParameter value should be propagated to query parameters`() = listOf(
        FormatParameter.Json to "_format=json",
        FormatParameter.ApplicationJson to "_format=application%2Fjson",
        FormatParameter.ApplicationFhirJson to "_format=application%2Ffhir%2Bjson",
        FormatParameter.Xml to "_format=xml",
        FormatParameter.ApplicationXml to "_format=application%2Fxml",
        FormatParameter.ApplicationFhirXml to "_format=application%2Ffhir%2Bxml",
    ).map { (format, expectedQueryParameter) ->
        DynamicTest.dynamicTest("Format value $format should lead to query parameter $expectedQueryParameter") {
            runBlocking {
                // Given
                server.enqueue(mockSuccessResponse)

                // When
                cdr.read(basicRequest.copy(format = format))

                // Then
                val request = server.takeRequest()
                request.requestUrl?.encodedQuery shouldBe expectedQueryParameter
            }
        }
    }

    @TestFactory
    fun `Pretty value should be propagated to query parameters`() = listOf(
        false to "_pretty=false",
        true to "_pretty=true",
    ).map { (prettyFlag, expectedQueryParameter) ->
        DynamicTest.dynamicTest("Pretty value $prettyFlag should lead to query parameter $expectedQueryParameter") {
            runBlocking {
                // Given
                server.enqueue(mockSuccessResponse)

                // When
                cdr.read(basicRequest.copy(pretty = prettyFlag))

                // Then
                val request = server.takeRequest()
                request.requestUrl?.encodedQuery shouldBe expectedQueryParameter
            }
        }
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
            cdr.read(basicRequest)
        }

        // Then
        server.takeRequest()
        exception.code shouldBe 500
        exception.message shouldBe """{"some":"error"}"""
    }

    @Test
    fun `Should throw when CDR does not include an ETag header`(): Unit = runBlocking {
        // Given
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setHeader("Last-Modified", "Tue, 21 Sep 2021 17:11:39 GMT")
            .setBody("""{"some":"value"}""")
        server.enqueue(mockedResponse)

        // When
        val exception = shouldThrowExactly<HttpException> {
            cdr.read(basicRequest)
        }

        // Then
        server.takeRequest()
        exception.code shouldBe 500
        exception.message shouldBe "ETag response header is missing"
    }

    @Test
    fun `Should throw when CDR does not include Last-Modified header`(): Unit = runBlocking {
        // Given
        val mockedResponse = MockResponse()
            .setResponseCode(200)
            .setHeader("ETag", """W/"abc"""")
            .setBody("""{"some":"value"}""")
        server.enqueue(mockedResponse)

        // When
        val exception = shouldThrowExactly<HttpException> {
            cdr.read(basicRequest)
        }
        server.takeRequest()

        // Then
        exception.code shouldBe 500
        exception.message shouldBe "Last-Modified response header is missing"
    }
}