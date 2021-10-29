/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk.query

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException

internal class DataItemDeleteQueryTest {
    @Nested
    inner class Validation {
        @Test
        fun `Should not throw when supplying both user and device`() {
            shouldNotThrowAny {
                DataItemDeleteQuery(
                    organizationQuery = OrganizationQuery("org1"),
                    idQuery = DataItemIdQuery("id"),
                    userQuery = UserQuery(
                        system = "s1",
                        value = "v1",
                    ),
                    deviceQuery = DeviceQuery(
                        system = "s2",
                        value = "v2",
                    ),
                )
            }
        }

        @Test
        fun `Should not throw when supplying user`() {
            shouldNotThrowAny {
                DataItemDeleteQuery(
                    organizationQuery = OrganizationQuery("org1"),
                    idQuery = DataItemIdQuery("id"),
                    userQuery = UserQuery(
                        system = "s1",
                        value = "v1",
                    ),
                )
            }
        }

        @Test
        fun `Should not throw when supplying device`() {
            shouldNotThrowAny {
                DataItemDeleteQuery(
                    organizationQuery = OrganizationQuery("org1"),
                    idQuery = DataItemIdQuery("id"),
                    userQuery = UserQuery(
                        system = "s1",
                        value = "v1",
                    ),
                )
            }
        }

        @Test
        fun `Should throw when supplying neither user nor device`() {
            val exception = shouldThrow<IllegalArgumentException> {
                DataItemDeleteQuery(
                    organizationQuery = OrganizationQuery("org1"),
                    idQuery = DataItemIdQuery("id"),
                )
            }
            exception.message shouldBe "At least one of user or device must be specified"
        }
    }
}
