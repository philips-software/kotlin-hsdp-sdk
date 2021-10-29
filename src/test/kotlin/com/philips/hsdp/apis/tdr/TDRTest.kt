/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr

import com.philips.hsdp.apis.iam.oauth2.domain.sdk.Token
import com.philips.hsdp.apis.support.HttpClient
import com.philips.hsdp.apis.support.HttpException
import com.philips.hsdp.apis.support.TokenRefresher
import com.philips.hsdp.apis.support.logging.MockLoggerFactory
import com.philips.hsdp.apis.support.logging.PlatformLoggerFactory
import com.philips.hsdp.apis.tdr.domain.conversion.toBatchBundle
/* ktlint-disable no-wildcard-imports */
import com.philips.hsdp.apis.tdr.domain.hsdp.*
import com.philips.hsdp.apis.tdr.domain.sdk.*
import com.philips.hsdp.apis.tdr.domain.sdk.query.*
/* ktlint-enable no-wildcard-imports */
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
/* ktlint-disable no-wildcard-imports */
import org.junit.jupiter.api.*
/* ktlint-enable no-wildcard-imports */
import java.io.InterruptedIOException
import java.time.Duration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TDRTest {

    init {
        PlatformLoggerFactory.registerConcreteFactory(MockLoggerFactory)
    }

    private val server = MockWebServer().apply {
        start()
    }
    private val tokenRefresherMock = mockk<TokenRefresher>()
    val httpClient = HttpClient(callTimeout = Duration.ofMillis(200)).apply {
        tokenRefresher = tokenRefresherMock
    }
    private val tdr = TDR(server.url("").toString(), httpClient)

    private val responseRequestId = "responseRequestId"
    private val requestIdDto = RequestIdDto(responseRequestId)
    private val createdResourceDto = CreatedResourceDto(
        resource = CreatedResource(
            location = "location",
            etag = "eTag",
            lastModified = "2021-09-21T17:11:39Z",
        ),
        requestId = responseRequestId,
    )

    @AfterAll
    fun afterAll() {
        server.shutdown()
    }

    @BeforeEach
    fun setup() {
        // Next stub cannot be put in the apply-block above, as there is a test that modifies
        // the returned token to have an empty accessToken, which would then lead to other tests
        // failing on an empty token.
        every { tokenRefresherMock.token } returns Token(accessToken = "22a34a6e-214c-4e3e-b85f-b4bbd1448613")
    }

    @Nested
    inner class GetDataItems {
        @Test
        fun `Should return a list of data items when providing TDR with a valid set of query parameters`(): Unit =
            runBlocking {
                val validBundle = Bundle(
                    id = "1",
                    meta = Meta(
                        lastUpdated = "2021-04-05T01:23:45.000Z",
                        versionId = "1.2",
                    ),
                    resourceType = ResourceType.Bundle,
                    type = Type.SearchSet,
                    total = 1,
                    _startAt = 0,
                    link = listOf(
                        Link(
                            relation = Relation.Next,
                            url = "foo?_startAt=10&_count=10",
                        )
                    ),
                    entry = listOf(
                        BundleEntry(
                            fullUrl = "bar",
                            resource = DataItem(
                                id = "2",
                                meta = Meta(
                                    lastUpdated = "2021-04-06T01:23:45.000Z",
                                    versionId = "1.3",
                                ),
                                timestamp = "2021-02-03T12:13:14.000Z",
                                sequenceNumber = 3,
                                device = Identifier("ds", "dv"),
                                user = Identifier("us", "uv"),
                                relatedPeripheral = Identifier("rps", "rpv"),
                                relatedUser = Identifier("rus", "ruv"),
                                dataType = Coding("cs", "cc"),
                                organization = "org1",
                                application = "Application",
                                proposition = "Proposition",
                                subscription = "Subscription",
                                dataSource = "source",
                                dataCategory = "category",
                                data = JsonObject(mapOf("value" to JsonPrimitive(10))),
                                blob = Blob(byteArrayOf(21, 22, 23, 24, 25)),
                                deleteTimestamp = "2021-03-03T12:13:14.000Z",
                                creationTimestamp = "2021-02-03T12:13:14.000Z",
                                tombstone = false,
                            ),
                            response = OperationResponse(
                                status = "",
                                location = "",
                                etag = "",
                                lastModified = "",
                                outcome = OperationOutcome(
                                    id = "",
                                    meta = Meta(
                                        lastUpdated = "2021-04-07T01:23:45.000Z",
                                        versionId = "1.4"
                                    ),
                                    issue = listOf(
                                        Issue(
                                            severity = Severity.Information,
                                            code = IssueCode.BusinessRule,
                                            details = CodeableConcept(
                                                text = "details"
                                            ),
                                            diagnostics = "diagnostics",
                                        )
                                    ),
                                ),
                            ),
                        )
                    ),
                )

                val dataItems = DataItemsDto(
                    data = listOf(
                        DataItemDto(
                            id = "2",
                            meta = Meta(
                                lastUpdated = "2021-04-06T01:23:45.000Z",
                                versionId = "1.3",
                            ),
                            timestamp = "2021-02-03T12:13:14.000Z",
                            sequenceNumber = 3,
                            device = Identifier("ds", "dv"),
                            user = Identifier("us", "uv"),
                            relatedPeripheral = Identifier("rps", "rpv"),
                            relatedUser = Identifier("rus", "ruv"),
                            dataType = Coding("cs", "cc"),
                            organization = "org1",
                            application = "Application",
                            proposition = "Proposition",
                            subscription = "Subscription",
                            dataSource = "source",
                            dataCategory = "category",
                            data = JsonObject(mapOf("value" to JsonPrimitive(10))),
                            blob = Blob(byteArrayOf(21, 22, 23, 24, 25)),
                            deleteTimestamp = "2021-03-03T12:13:14.000Z",
                            creationTimestamp = "2021-02-03T12:13:14.000Z",
                            tombstone = false,
                            link = SelfLinkDto("bar"),
                        )
                    ),
                    pagination = PaginationDto(
                        offset = 0,
                        limit = 10,
                    ),
                    requestId = responseRequestId
                )

                // Given
                val mockedResponse = MockResponse()
                    .setResponseCode(200)
                    .setBody(TDR.json.encodeToString(validBundle))
                    .setHeader("Hsdp-Request-Id", responseRequestId)

                server.enqueue(mockedResponse)

                // When
                val result = tdr.getDataItems(DataItemQuery(OrganizationQuery("org1")))
                val request = server.takeRequest()

                // Then
                request.requestUrl?.encodedPath shouldBe "/store/tdr/DataItem"
                request.requestUrl?.encodedQuery shouldBe "organization=org1"
                request.method shouldBe "GET"
                request.headers.toMultimap() shouldContainAll mapOf(
                    "authorization" to listOf("Bearer ${httpClient.token.accessToken}"),
                    "api-version" to listOf("5"),
                    "accept" to listOf("application/json; charset=utf-8"),
                )
                result shouldBe dataItems
            }

        @Test
        fun `Should throw when TDR returns a 500`() = runBlocking {
            // Given
            val mockedResponse = MockResponse()
                .setResponseCode(500)
                .setBody("Internal server error")

            server.enqueue(mockedResponse)

            // When
            val exception = assertThrows<HttpException> {
                tdr.getDataItems(DataItemQuery(OrganizationQuery(("org1"))))
            }
            val request = server.takeRequest()

            // Then
            request.requestUrl?.encodedPath shouldBe "/store/tdr/DataItem"
            request.requestUrl?.encodedQuery shouldBe "organization=org1"
            request.method shouldBe "GET"
            request.headers.toMultimap() shouldContainAll mapOf(
                "authorization" to listOf("Bearer ${httpClient.token.accessToken}"),
                "api-version" to listOf("5"),
                "accept" to listOf("application/json; charset=utf-8"),
            )
            exception.code shouldBe 500
            exception.message shouldBe "Internal server error"
        }

        @Test
        fun `Should throw when TDR returns an invalid payload`(): Unit = runBlocking {
            // Given
            val mockedResponse = MockResponse()
                .setResponseCode(200)
                .setBody("""{"invalid":"payload"}""")
                .setHeader("Hsdp-Request-Id", responseRequestId)

            server.enqueue(mockedResponse)

            // When
            val exception = assertThrows<SerializationException> {
                tdr.getDataItems(DataItemQuery(OrganizationQuery(("org1"))))
            }
            val request = server.takeRequest()

            // Then
            request.requestUrl?.encodedPath shouldBe "/store/tdr/DataItem"
            request.requestUrl?.encodedQuery shouldBe "organization=org1"
            request.method shouldBe "GET"
            request.headers.toMultimap() shouldContainAll mapOf(
                "authorization" to listOf("Bearer ${httpClient.token.accessToken}"),
                "api-version" to listOf("5"),
                "accept" to listOf("application/json; charset=utf-8"),
            )
            exception.message shouldStartWith "Unexpected JSON token at offset 2: Encountered an unknown key 'invalid'."
        }

        @Test
        fun `Should throw an IllegalArgumentException when access token is empty`(): Unit = runBlocking {
            // Given
            every { httpClient.token } returns Token(accessToken = "")

            // When
            val exception = assertThrows<IllegalArgumentException> {
                tdr.getDataItems(DataItemQuery(OrganizationQuery(("org1"))))
            }

            // Then
            exception.message shouldBe "An access token is required"
        }

        @Test
        fun `Should throw a InterruptedIOException when the server does not respond`(): Unit = runBlocking {
            val mockedResponse = MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE)

            server.enqueue(mockedResponse)

            // When/Then
            val exception = shouldThrow<InterruptedIOException> {
                tdr.getDataItems(DataItemQuery(OrganizationQuery(("org1"))))
            }
            server.takeRequest()

            exception.message shouldBe "timeout"
        }
    }

    @Nested
    inner class PostDataItem {
        private val newDataItem = NewDataItemDto(
            timestamp = "2021-09-21T17:11:39.000Z",
            user = Identifier(
                system = "us1",
                value = "uv1",
            ),
            dataType = Coding(
                system = "s1",
                code = "c1",
            ),
            organization = "org1",
            data = JsonObject(emptyMap()),
            blob = Blob(byteArrayOf(21, 22, 23, 24, 25)),
        )

        @Test
        fun `Should not throw when TDR reports 201`() = runBlocking {
            // Given
            val jsonPayload = Json.encodeToString(newDataItem)

            val mockedResponse = MockResponse()
                .setResponseCode(201)
                .setHeader("Location", "location")
                .setHeader("ETag", "eTag")
                .setHeader("Last-Modified", "Tue, 21 Sep 2021 17:11:39 UTC")
                .setHeader("Hsdp-Request-Id", responseRequestId)

            server.enqueue(mockedResponse)

            // When
            shouldNotThrowAny {
                val result = tdr.storeDataItem(newDataItem)
                result shouldBe createdResourceDto
            }
            val request = server.takeRequest()

            // Then
            request.requestUrl?.encodedPath shouldBe "/store/tdr/DataItem"
            request.body.readByteArray().toString(Charsets.UTF_8) shouldBe jsonPayload
            request.method shouldBe "POST"
            request.headers.toMultimap() shouldContainAll mapOf(
                "authorization" to listOf("Bearer ${httpClient.token.accessToken}"),
                "api-version" to listOf("5"),
                "Content-Type" to listOf("application/json; charset=utf-8"),
                "accept" to listOf("application/json; charset=utf-8"),
            )
        }

        @Test
        fun `Should throw when TDR returns a status 500`(): Unit = runBlocking {
            val mockedResponse = MockResponse()
                .setResponseCode(500)

            server.enqueue(mockedResponse)

            // Then
            shouldThrow<HttpException> {
                // When
                tdr.storeDataItem(newDataItem)
            }
            server.takeRequest()
        }
    }

    @Nested
    inner class DeleteDataItem {
        private val deleteDataItemQuery = DataItemDeleteQuery(
            organizationQuery = OrganizationQuery("org1"),
            idQuery = DataItemIdQuery("id"),
            userQuery = UserQuery("s1", "v1")
        )

        @Test
        fun `Should not throw when TDR reports 204`() = runBlocking {
            // Given
            val mockedResponse = MockResponse()
                .setResponseCode(204)
                .setHeader("Hsdp-Request-Id", responseRequestId)

            server.enqueue(mockedResponse)

            // When
            shouldNotThrowAny {
                val result = tdr.deleteDataItem(deleteDataItemQuery)
                result shouldBe requestIdDto
            }
            val request = server.takeRequest()

            // Then
            request.requestUrl?.encodedPath shouldBe "/store/tdr/DataItem"
            request.method shouldBe "DELETE"
            request.headers.toMultimap() shouldContainAll mapOf(
                "authorization" to listOf("Bearer ${httpClient.token.accessToken}"),
                "api-version" to listOf("5"),
                "accept" to listOf("application/json; charset=utf-8"),
            )
        }

        @Test
        fun `Should return the request id in the response headers when passing in a request id to the method`(): Unit =
            runBlocking {
                // Given
                val requestId = "requestId"
                val mockedResponse = MockResponse()
                    .setResponseCode(204)
                    .setHeader("Hsdp-Request-Id", responseRequestId)

                server.enqueue(mockedResponse)

                // When
                tdr.deleteDataItem(deleteDataItemQuery, requestId)
                val request = server.takeRequest()

                // Then
                request.headers.toMultimap() shouldContain Pair("HSDP-Request-ID", listOf(requestId))
            }

        @Test
        fun `Should throw when TDR returns a status 500`(): Unit = runBlocking {
            val mockedResponse = MockResponse()
                .setResponseCode(500)

            server.enqueue(mockedResponse)

            // Then
            shouldThrow<HttpException> {
                // When
                tdr.deleteDataItem(deleteDataItemQuery)
            }
            server.takeRequest()
        }
    }

    @Nested
    inner class PatchDataItem {
        private val patchQuery = DataItemPatchQuery(
            organizationQuery = OrganizationQuery("org1"),
            idQuery = DataItemIdQuery("id"),
            userQuery = UserQuery(
                system = "us1",
                value = "uv1",
            ),
            deviceQuery = DeviceQuery(
                system = "s1",
                value = "c1",
            ),
        )
        private val patchDocument = listOf(
            PatchDocument(
                op = Operation.Add,
                path = "/data/foo",
                value = JsonPrimitive("2"),
            ),
            PatchDocument(
                op = Operation.Replace,
                path = "/data/bar",
                value = JsonPrimitive(5),
            ),
        )

        @Test
        fun `Should not throw when TDR reports 204`() = runBlocking {
            // Given
            val jsonPayload = Json.encodeToString(patchDocument)

            val mockedResponse = MockResponse()
                .setResponseCode(204)
                .setHeader("Hsdp-Request-Id", responseRequestId)

            server.enqueue(mockedResponse)

            // When
            shouldNotThrowAny {
                val result = tdr.patchDataItem(patchQuery, patchDocument)
                result shouldBe requestIdDto
            }
            val request = server.takeRequest()

            // Then
            request.requestUrl?.encodedPath shouldBe "/store/tdr/DataItem"
            request.body.readByteArray().toString(Charsets.UTF_8) shouldBe jsonPayload
            request.method shouldBe "PATCH"
            request.headers.toMultimap() shouldContainAll mapOf(
                "authorization" to listOf("Bearer ${httpClient.token.accessToken}"),
                "api-version" to listOf("5"),
                "Content-Type" to listOf("application/json; charset=utf-8"),
                "accept" to listOf("application/json; charset=utf-8"),
            )
        }

        @Test
        fun `Should throw when TDR returns a status 500`(): Unit = runBlocking {
            val mockedResponse = MockResponse()
                .setResponseCode(500)

            server.enqueue(mockedResponse)

            // Then
            shouldThrow<HttpException> {
                // When
                tdr.patchDataItem(patchQuery, patchDocument)
            }
            server.takeRequest()
        }
    }

    @Nested
    inner class PostDataItems {
        private val newDataItems = NewDataItemsDto(
            dataItems = listOf(
                NewDataItemDto(
                    timestamp = "2021-08-28T23:45:43.000Z",
                    user = Identifier(
                        system = "us1",
                        value = "uv1",
                    ),
                    dataType = Coding(
                        system = "s1",
                        code = "c1",
                    ),
                    organization = "org1",
                    data = JsonObject(emptyMap()),
                    blob = Blob(byteArrayOf(21, 22, 23, 24, 25)),
                ),
                NewDataItemDto(
                    timestamp = "2021-08-28T23:45:44.000Z",
                    user = Identifier(
                        system = "us1",
                        value = "uv1",
                    ),
                    dataType = Coding(
                        system = "s1",
                        code = "c1",
                    ),
                    organization = "org1",
                    data = JsonObject(emptyMap()),
                    blob = Blob(byteArrayOf(31, 32, 33, 34, 35)),
                ),
            ),
        )

        private val createdDataItemsDto = CreatedDataItemsDto(
            dataItems = listOf(
                CreatedResource(
                    location = "url1",
                    etag = "1",
                    lastModified = "2021-09-20T16:01:02.023Z",
                ),
                CreationFailure(
                    issues = listOf(
                        Issue(
                            severity = Severity.Error,
                            code = IssueCode.Invalid,
                            details = CodeableConcept("The DataItem could not be created because it already exists.")
                        )
                    ),
                ),
            ),
            requestId = responseRequestId,
        )

        @Test
        fun `Should not throw when TDR reports 201`() = runBlocking {
            // Given
            val jsonPayload = Json.encodeToString(newDataItems.toBatchBundle())
            val tdrResponse = """{
                "type": "batch",
                "resourceType": "Bundle",
                "total": 2,
                "entry": [
                    {
                        "status": 201,
                        "location": "url1",
                        "etag": "1",
                        "lastModified": "2021-09-20T16:01:02.023Z"
                    },
                    {
                        "status": 409,
                        "outcome": {
                            "issue": [
                                {
                                    "severity": "error",
                                    "code": "invalid",
                                    "details": {
                                        "text": "The DataItem could not be created because it already exists."
                                    }
                                }
                            ],
                            "resourceType": "OperationOutcome"
                        }
                    }
                ]
            }
            """.trimIndent()

            val mockedResponse = MockResponse()
                .setResponseCode(201)
                .setBody(tdrResponse)
                .setHeader("Hsdp-Request-Id", responseRequestId)

            server.enqueue(mockedResponse)

            // When
            shouldNotThrowAny {
                val result = tdr.storeDataItems(newDataItems)
                result shouldBe createdDataItemsDto
            }
            val request = server.takeRequest()

            // Then
            request.requestUrl?.encodedPath shouldBe "/store/tdr/DataItems"
            request.body.readByteArray().toString(Charsets.UTF_8) shouldBe jsonPayload
            request.method shouldBe "POST"
            request.headers.toMultimap() shouldContainAll mapOf(
                "authorization" to listOf("Bearer ${httpClient.token.accessToken}"),
                "api-version" to listOf("5"),
                "Content-Type" to listOf("application/json; charset=utf-8"),
                "accept" to listOf("application/json; charset=utf-8"),
            )
        }

        @Test
        fun `Should throw when TDR returns a status 500`(): Unit = runBlocking {
            val mockedResponse = MockResponse()
                .setResponseCode(500)
                .setHeader("Hsdp-Request-Id", responseRequestId)

            server.enqueue(mockedResponse)

            // Then
            shouldThrow<HttpException> {
                // When
                tdr.storeDataItems(newDataItems)
            }
            server.takeRequest()
        }

        @Test
        fun `Should throw when TDR returns invalid JSON`(): Unit = runBlocking {
            val mockedResponse = MockResponse()
                .setResponseCode(200)
                .setBody("""{"invalid json"}""")
                .setHeader("Hsdp-Request-Id", responseRequestId)

            server.enqueue(mockedResponse)

            // Then
            val exception = shouldThrow<SerializationException> {
                // When
                tdr.storeDataItems(newDataItems)
            }
            server.takeRequest()

            exception.message shouldStartWith "Unexpected JSON token at offset 14: Expected semicolon"
        }
    }

    @Nested
    inner class PostContract {
        private val newContract = NewContractDto(
            schema = JsonObject(emptyMap()),
            dataType = Coding(
                system = "s1",
                code = "c1",
            ),
            organization = "org1",
            deletePolicy = DeletePolicy(
                duration = 1,
                unit = TimeUnit.Month,
            )
        )

        @Test
        fun `Should not throw when TDR reports 201`() = runBlocking {
            // Given
            val jsonPayload = Json.encodeToString(newContract)

            val mockedResponse = MockResponse()
                .setResponseCode(201)
                .setHeader("Location", "location")
                .setHeader("ETag", "eTag")
                .setHeader("Last-Modified", "Tue, 21 Sep 2021 17:11:39 UTC")
                .setHeader("Hsdp-Request-Id", responseRequestId)

            server.enqueue(mockedResponse)

            // When
            shouldNotThrowAny {
                val result = tdr.storeContract(newContract)
                result shouldBe createdResourceDto
            }
            val request = server.takeRequest()

            // Then
            request.requestUrl?.encodedPath shouldBe "/store/tdr/Contract"
            request.body.readByteArray().toString(Charsets.UTF_8) shouldBe jsonPayload
            request.method shouldBe "POST"
            request.headers.toMultimap() shouldContainAll mapOf(
                "authorization" to listOf("Bearer ${httpClient.token.accessToken}"),
                "api-version" to listOf("5"),
                "Content-Type" to listOf("application/json; charset=utf-8"),
                "accept" to listOf("application/json; charset=utf-8"),
            )
        }

        @Test
        fun `Should throw when TDR returns a status 500`(): Unit = runBlocking {
            val mockedResponse = MockResponse()
                .setResponseCode(500)

            server.enqueue(mockedResponse)

            // Then
            shouldThrow<HttpException> {
                // When
                tdr.storeContract(newContract)
            }
            server.takeRequest()
        }
    }

    @Nested
    inner class GetContracts {
        @Test
        fun `Should return a list of contracts when TDR response is 200`() = runBlocking {
            // Given
            val bundle = Bundle(
                id = "1",
                meta = Meta(
                    lastUpdated = "2021-04-05T01:23:45.000Z",
                    versionId = "1.2",
                ),
                resourceType = ResourceType.Bundle,
                type = Type.SearchSet,
                total = 1,
                _startAt = 0,
                link = listOf(
                    Link(
                        relation = Relation.Next,
                        url = "foo?_startAt=10&_count=10",
                    )
                ),
                entry = listOf(
                    BundleEntry(
                        fullUrl = "bar",
                        resource = Contract(
                            id = "",
                            meta = Meta(
                                lastUpdated = "2021-04-06T01:23:45.000Z",
                                versionId = "1.3",
                            ),
                            schema = JsonObject(
                                mapOf(
                                    "schema" to JsonObject(
                                        mapOf(
                                            "\$schema" to JsonPrimitive("http://json-schema.org/draft-04/schema#"),
                                            "type" to JsonPrimitive("object"),
                                        )
                                    )
                                )
                            ),
                            dataType = Coding(
                                system = "sys",
                                code = "val",
                            ),
                            organization = "org",
                            sendNotifications = false,
                            notificationServiceTopicId = "topic",
                            deletePolicy = DeletePolicy(
                                31,
                                TimeUnit.Day,
                            ),
                        ),
                        response = OperationResponse(
                            status = "",
                            location = "",
                            etag = "",
                            lastModified = "",
                            outcome = OperationOutcome(
                                id = "",
                                meta = Meta(
                                    lastUpdated = "2021-04-07T01:23:45.000Z",
                                    versionId = "1.4"
                                ),
                                issue = listOf(
                                    Issue(
                                        severity = Severity.Information,
                                        code = IssueCode.BusinessRule,
                                        details = CodeableConcept(
                                            text = "details"
                                        ),
                                        diagnostics = "diagnostics",
                                    )
                                )
                            )
                        ),
                    )
                )
            )

            val contracts = ContractsDto(
                data = listOf(
                    ContractDto(
                        id = "",
                        meta = Meta(
                            lastUpdated = "2021-04-06T01:23:45.000Z",
                            versionId = "1.3",
                        ),
                        schema = JsonObject(
                            mapOf(
                                "schema" to JsonObject(
                                    mapOf(
                                        "\$schema" to JsonPrimitive("http://json-schema.org/draft-04/schema#"),
                                        "type" to JsonPrimitive("object"),
                                    )
                                )
                            )
                        ),
                        dataType = Coding(
                            system = "sys",
                            code = "val",
                        ),
                        organization = "org",
                        sendNotifications = false,
                        notificationServiceTopicId = "topic",
                        deletePolicy = DeletePolicy(
                            31,
                            TimeUnit.Day,
                        ),
                        link = SelfLinkDto("bar")
                    )
                ),
                pagination = PaginationDto(
                    offset = 0,
                    limit = 10,
                ),
                requestId = responseRequestId
            )

            val body = TDR.json.encodeToString(bundle)
            val mockedResponse = MockResponse()
                .setResponseCode(200)
                .setBody(body)
                .setHeader("Hsdp-Request-Id", responseRequestId)

            server.enqueue(mockedResponse)

            // When
            val result = tdr.getContracts(ContractQuery(organizationQuery = OrganizationQuery("org1")))
            val request = server.takeRequest()

            // Then
            request.requestUrl?.encodedPath shouldBe "/store/tdr/Contract"
            request.requestUrl?.encodedQuery shouldBe "organization=org1"
            request.method shouldBe "GET"
            request.headers.toMultimap() shouldContainAll mapOf(
                "authorization" to listOf("Bearer ${httpClient.token.accessToken}"),
                "api-version" to listOf("5"),
                "accept" to listOf("application/json; charset=utf-8"),
            )
            result shouldBe contracts
        }

        @Test
        fun `Should throw when TDR returns a 500`() = runBlocking {
            // Given
            val mockedResponse = MockResponse()
                .setResponseCode(500)
                .setBody("Internal server error")

            server.enqueue(mockedResponse)

            // When
            val exception = assertThrows<HttpException> {
                tdr.getContracts(ContractQuery(organizationQuery = OrganizationQuery("org1")))
            }
            val request = server.takeRequest()

            // Then
            request.requestUrl?.encodedPath shouldBe "/store/tdr/Contract"
            request.method shouldBe "GET"
            request.headers.toMultimap() shouldContainAll mapOf(
                "authorization" to listOf("Bearer ${httpClient.token.accessToken}"),
                "api-version" to listOf("5"),
                "accept" to listOf("application/json; charset=utf-8"),
            )
            exception.code shouldBe 500
            exception.message shouldBe "Internal server error"
        }

        @Test
        fun `Should throw when TDR returns an invalid payload`(): Unit = runBlocking {
            // Given
            val mockedResponse = MockResponse()
                .setResponseCode(200)
                .setBody("""{"invalid":"payload"}""")
                .setHeader("Hsdp-Request-Id", responseRequestId)

            server.enqueue(mockedResponse)

            // When
            val exception = assertThrows<SerializationException> {
                tdr.getContracts(ContractQuery(organizationQuery = OrganizationQuery("org1")))
            }
            val request = server.takeRequest()

            // Then
            request.requestUrl?.encodedPath shouldBe "/store/tdr/Contract"
            request.method shouldBe "GET"
            request.headers.toMultimap() shouldContainAll mapOf(
                "authorization" to listOf("Bearer ${httpClient.token.accessToken}"),
                "api-version" to listOf("5"),
                "accept" to listOf("application/json; charset=utf-8"),
            )
            exception.message shouldStartWith "Unexpected JSON token at offset 2: Encountered an unknown key 'invalid'."
        }
    }
}
