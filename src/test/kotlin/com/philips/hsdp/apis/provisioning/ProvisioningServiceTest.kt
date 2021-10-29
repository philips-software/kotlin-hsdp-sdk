/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.provisioning

import com.philips.hsdp.apis.iam.oauth2.domain.sdk.Token
/* ktlint-disable no-wildcard-imports */
import com.philips.hsdp.apis.provisioning.domain.hsdp.*
import com.philips.hsdp.apis.provisioning.domain.sdk.*
/* ktlint-enable no-wildcard-imports */
import com.philips.hsdp.apis.support.HttpClient
import com.philips.hsdp.apis.support.HttpException
import com.philips.hsdp.apis.support.TokenRefresher
import com.philips.hsdp.apis.support.logging.MockLoggerFactory
import com.philips.hsdp.apis.support.logging.PlatformLoggerFactory
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
/* ktlint-disable no-wildcard-imports */
import org.junit.jupiter.api.*
/* ktlint-enable no-wildcard-imports */
import java.io.InterruptedIOException
import java.time.Duration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ProvisioningServiceTest {

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
    private val provisioningService = ProvisioningService(server.url("").toString(), httpClient)

    private val responseTransactionId = "responseRequestId"

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
    inner class CreateIdentity {
        private val validNewDeviceEntity = NewDeviceIdentity(
            type = "type",
            identityType = "device",
            deviceAttributes = DeviceAttributes(
                serialNumber = "1234567",
                materialNumber = "abcde12345",
                systemIdentifier = "xxx|yyy",
//                    deviceExtId = DeviceExtId(
//                        system = "system",
//                        value = "value",
//                        type = Type(
//                            code = "code",
//                            text = "text in the type field",
//                        ),
//                    ),
                additionalAttributes = listOf(
                    Parameter(name = "nameTag", valueString = "TestHub_2225"),
                    Parameter(name = "parentNameTag", valueString = "PRS_RAM_IoTHub"),
                    Parameter(name = "someLong", valueDecimal = 3L),
                    Parameter(
                        name = "someReference",
                        valueReference = Reference(
                            reference = "reference",
                            display = "display",
                        )
                    ),
                    Parameter(name = "someDateTime", valueDateTime = "2021-09-20T13:14:15.000Z"),
                )
            ),
            additionalAttributes = listOf(
                Parameter(name = "someBoolean", valueBoolean = true),
                Parameter(name = "someInt", valueInteger = 2),
                Parameter(name = "someCode", valueCode = "xxx|yyy"),
                Parameter(
                    name = "someIdentifier",
                    valueIdentifier = Identifier(
                        system = "system",
                        value = "value",
                    )
                ),
            )
        )
        private val validNewParameters = Parameters(
            parameter = listOf(
                Parameter(name = "type", valueString = "type"),
                Parameter(name = "identityType", valueString = "device"),
                Parameter(
                    name = "DeviceAttributes",
                    part = listOf(
                        Parameter(name = "serialNumber", valueString = "1234567"),
                        Parameter(name = "materialNumber", valueString = "abcde12345"),
                        Parameter(name = "systemIdentifier", valueString = "xxx|yyy"),
//                        Parameter(name = "deviceExtId", part = listOf(
//                            Parameter(name = "system", valueString = "system"),
//                            Parameter(name = "value", valueString = "value"),
//                            Parameter(name = "type", value = Type(
//                                    code = "code",
//                                    text = "text in the type field",
//                                ),
//                        )),
                        Parameter(name = "nameTag", valueString = "TestHub_2225"),
                        Parameter(name = "parentNameTag", valueString = "PRS_RAM_IoTHub"),
                        Parameter(name = "someLong", valueDecimal = 3L),
                        Parameter(
                            name = "someReference",
                            valueReference = Reference(
                                reference = "reference",
                                display = "display",
                            )
                        ),
                        Parameter(name = "someDateTime", valueDateTime = "2021-09-20T13:14:15.000Z"),
                    )
                ),
                Parameter(name = "someBoolean", valueBoolean = true),
                Parameter(name = "someInt", valueInteger = 2),
                Parameter(name = "someCode", valueCode = "xxx|yyy"),
                Parameter(
                    name = "someIdentifier",
                    valueIdentifier = Identifier(
                        system = "system",
                        value = "value",
                    )
                ),
            )
        )
        private val responseIdentityParameters = listOf(
            Parameter(name = "identitySignature", valueString = "identitySignature"),
            Parameter(name = "loginId", valueString = "loginId"),
            Parameter(name = "password", valueString = "password"),
            Parameter(name = "HSDPId", valueString = "hsdpId"),
            Parameter(name = "OAuthClientId", valueString = "oauthClientId"),
            Parameter(name = "OAuthClientSecret", valueString = "oauthClientSecret"),
        )

        private val validReturnedParameters = validNewParameters.copy(
            parameter = validNewParameters.parameter + responseIdentityParameters
        )
        private val validDeviceEntity = with(validNewDeviceEntity) {
            DeviceIdentity(
                type = type,
                identityType = identityType,
                loginId = "loginId",
                password = "password",
                hsdpId = "hsdpId",
                oauthClientId = "oauthClientId",
                oauthClientSecret = "oauthClientSecret",
                identitySignature = "identitySignature",
                deviceAttributes = deviceAttributes,
                additionalAttributes = additionalAttributes,
            )
        }

        @Test
        fun `Should return a device identity with transactionId when HSDP service responds with 200`() = runBlocking {
            // Given
            val body = ProvisioningService.json.encodeToString(validReturnedParameters as Resource)
            val mockedResponse = MockResponse()
                .setResponseCode(200)
                .setBody(body)
                .setHeader("transactionId", responseTransactionId)

            server.enqueue(mockedResponse)

            // When
            val result = provisioningService.createIdentity(validNewDeviceEntity)
            val request = server.takeRequest()

            // Then
            request.requestUrl?.encodedPath shouldBe "/\$create-identity"
            request.method shouldBe "POST"
            request.headers.toMultimap() shouldContainAll mapOf(
                "authorization" to listOf("Bearer ${httpClient.token.accessToken}"),
                "api-version" to listOf("1"),
                "accept" to listOf("application/json"),
            )
            result shouldBe ProvisioningResponse(data = validDeviceEntity, transactionId = responseTransactionId)
        }

        @Test
        fun `Should throw an HttpException when the returned status is not 2xx`(): Unit = runBlocking {
            val mockedResponse = MockResponse()
                .setResponseCode(500)

            server.enqueue(mockedResponse)

            // Then
            shouldThrow<HttpException> {
                // When
                provisioningService.createIdentity(validNewDeviceEntity)
            }
            server.takeRequest()
        }

        @Test
        fun `Should throw an IllegalArgumentException when access token is empty`(): Unit = runBlocking {
            // Given
            every { httpClient.token } returns Token(accessToken = "")

            // When
            val exception = assertThrows<IllegalArgumentException> {
                provisioningService.createIdentity(validNewDeviceEntity)
            }

            // Then
            exception.message shouldBe "An access token is required"
        }

        @Test
        fun `Should throw a SerializationException when the returned JSON is invalid`(): Unit = runBlocking {
            val mockedResponse = MockResponse()
                .setResponseCode(200)
                .setBody("""{"invalid json"}""")
                .setHeader("transactionId", responseTransactionId)

            server.enqueue(mockedResponse)

            // When/Then
            val exception = shouldThrow<SerializationException> {
                provisioningService.createIdentity(validNewDeviceEntity)
            }
            server.takeRequest()

            exception.message shouldStartWith "Unexpected JSON token at offset 15: Expected semicolon"
        }

        @Test
        fun `Should throw a InterruptedIOException when the server does not respond`(): Unit = runBlocking {
            val mockedResponse = MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE)

            server.enqueue(mockedResponse)

            // When/Then
            val result = shouldThrow<InterruptedIOException> {
                provisioningService.createIdentity(validNewDeviceEntity)
            }
            server.takeRequest()

            result.message shouldBe "timeout"
        }
    }
}
