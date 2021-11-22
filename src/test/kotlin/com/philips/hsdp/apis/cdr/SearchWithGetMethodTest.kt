/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.cdr

import com.philips.hsdp.apis.cdr.domain.sdk.*
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class SearchWithGetMethodTest : CdrTestBase() {

    private val searchMethod = SearchMethod.Get
    private val basicRequest = SearchRequest(
        resourceType = "Patient",
    )
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
        val result = cdr.search(basicRequest, searchMethod)

        // Then
        val request = server.takeRequest()
        request.requestUrl?.encodedPath shouldBe "/Patient"
        request.requestUrl?.encodedQuery should beNull()
        request.method shouldBe "GET"
        request.headers.toMultimap() shouldContainAll mapOf(
            "authorization" to listOf("Bearer ${httpClient.token.accessToken}"),
            "api-version" to listOf("1"),
            "accept" to listOf("application/fhir+json; charset=UTF-8; fhirVersion=3.0"),
        )
        result shouldBe SearchResponse(200, """{"some":"value"}""")
    }

    @Test
    fun `Should return the response when including the compartment`(): Unit = runBlocking {
        // Given
        server.enqueue(mockSuccessResponse)

        // When
        val result = cdr.search(basicRequest.copy(compartment = Compartment("ctype", "cid")), searchMethod)

        // Then
        val request = server.takeRequest()
        request.requestUrl?.encodedPath shouldBe "/ctype/cid/Patient"
        request.requestUrl?.encodedQuery should beNull()
        request.method shouldBe "GET"
        request.headers.toMultimap() shouldContainAll mapOf(
            "authorization" to listOf("Bearer ${httpClient.token.accessToken}"),
            "api-version" to listOf("1"),
            "accept" to listOf("application/fhir+json; charset=UTF-8; fhirVersion=3.0"),
        )
        result shouldBe SearchResponse(200, """{"some":"value"}""")
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
                cdr.search(basicRequest.copy(format = format), searchMethod)

                // Then
                val request = server.takeRequest()
                request.requestUrl?.encodedQuery shouldBe expectedQueryParameter
            }
        }
    }

    @Test
    fun `Should include the query parameters when providing them`(): Unit = runBlocking {
        // Given
        server.enqueue(mockSuccessResponse)

        // When
        cdr.search(basicRequest.copy(queryParameters = listOf(QueryParameter("k", "v"))), searchMethod)
        val request = server.takeRequest()

        // Then
        request.requestUrl?.encodedQuery shouldBe "k=v"
    }

}