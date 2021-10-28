/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk.query

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DataItemQueryTest {
    @Nested
    inner class Init {
        @Test
        fun `Should not throw when supplying the mandatory parameter`() {
            shouldNotThrowAny {
                DataItemQuery(organizationQuery = OrganizationQuery("foo"))
            }
        }

        @Test
        fun `Should not throw when supplying the dynamic string parameters and a datatype`() {
            shouldNotThrowAny {
                DataItemQuery(
                    organizationQuery = OrganizationQuery("foo"),
                    dataTypeQuery = DataTypeQuery("foo", "bar"),
                    dynamicStringQuery = listOf(DynamicStringQuery("data.foz:exact", "baz"))
                )
            }
        }

        @Test
        fun `Should not throw when supplying the dynamic integer parameters and a datatype`() {
            shouldNotThrowAny {
                DataItemQuery(
                    organizationQuery = OrganizationQuery("foo"),
                    dataTypeQuery = DataTypeQuery("foo", "bar"),
                    dynamicIntegerQuery = listOf(DynamicIntegerQuery("data.foz", 1))
                )
            }
        }

        @Test
        fun `Should not throw when supplying the dynamic boolean parameters and a datatype`() {
            shouldNotThrowAny {
                DataItemQuery(
                    organizationQuery = OrganizationQuery("foo"),
                    dataTypeQuery = DataTypeQuery("foo", "bar"),
                    dynamicBooleanQuery = listOf(DynamicBooleanQuery("data.foz", true))
                )
            }
        }

        @Test
        fun `Should throw when supplying the dynamic string parameters without a datatype`() {
            val exception = shouldThrow<IllegalArgumentException> {
                DataItemQuery(
                    organizationQuery = OrganizationQuery("foo"),
                    dynamicStringQuery = listOf(DynamicStringQuery("data.foz:exact", "baz"))
                )
            }
            exception.message shouldBe "Dynamic queries require the datatype to be specified as well"
        }

        @Test
        fun `Should throw when supplying the dynamic integer parameters without a datatype`() {
            val exception = shouldThrow<IllegalArgumentException> {
                DataItemQuery(
                    organizationQuery = OrganizationQuery("foo"),
                    dynamicIntegerQuery = listOf(DynamicIntegerQuery("data.foz", 1))
                )
            }
            exception.message shouldBe "Dynamic queries require the datatype to be specified as well"
        }

        @Test
        fun `Should throw when supplying the dynamic boolean parameters without a datatype`() {
            val exception = shouldThrow<IllegalArgumentException> {
                DataItemQuery(
                    organizationQuery = OrganizationQuery("foo"),
                    dynamicBooleanQuery = listOf(DynamicBooleanQuery("data.foz", true))
                )
            }
            exception.message shouldBe "Dynamic queries require the datatype to be specified as well"
        }

//        NOTE: According to the API spec either device or user is required, but TDR allows both to be absent
//        @Test
//        fun `Should not throw when device query parameter is not null`() {
//            val query = DataItemQuery(
//                organizationQuery = OrganizationQuery("foo"),
//                deviceQuery = DeviceQuery("c", "d"),
//            )
//            query shouldNotBe null
//        }
//
//        @Test
//        fun `Should not throw when user and device query parameters are not null`() {
//            val query = DataItemQuery(
//                organizationQuery = OrganizationQuery("foo"),
//                userQuery = UserQuery("a", "b"),
//                deviceQuery = DeviceQuery("c", "d"),
//            )
//            query shouldNotBe null
//        }
//
//        @Test
//        fun `Should throw when both user and device query parameters are null`() {
//            val exception = shouldThrow<Exception> {
//                DataItemQuery(
//                    organizationQuery = OrganizationQuery("foo"),
//                )
//            }
//            exception.message shouldBe "Failed requirement."
//        }
    }

    @Nested
    inner class ToQueryParameter {
        @Test
        fun `Should properly convert the query to a QueryParameter when just supplying the mandatory parameters`() {
            val query = DataItemQuery(
                OrganizationQuery("org1"),
            )

            query.getQueryParameters() shouldBe listOf(
                QueryParameter("organization", "org1"),
            )
        }

        @Test
        fun `Should properly convert the query to a QueryParameters when supplying all optional parameters`() {
            val query = DataItemQuery(
                organizationQuery = OrganizationQuery("org1"),
                userQuery = UserQuery("s1", "v1"),
                deviceQuery = DeviceQuery("s2", "v2"),
                dataTypeQuery = DataTypeQuery("s3", "v3"),
                idQuery = DataItemIdQuery("i4"),
                timestampQuery = TimestampQuery("2021-06-07T12:13:14.000Z"),
                sequenceNumberQuery = SequenceNumberQuery(5),
                relatedUserQuery = RelatedUserQuery("s6", "v6"),
                relatedPeripheralQuery = RelatedPeripheralQuery("s7", "v7"),
                propositionQuery = PropositionQuery("p8"),
                applicationQuery = ApplicationQuery("a9"),
                subscriptionQuery = SubscriptionQuery("s10"),
                dataCategoryQuery = DataCategoryQuery("dc11"),
                dataSourceQuery = DataSourceQuery("ds12"),
                dynamicStringQuery = listOf(
                    DynamicStringQuery("data.field1:exact", "f13"),
                    DynamicStringQuery("data.field2:exact", listOf("f14", "f15")),
                ),
                dynamicIntegerQuery = listOf(
                    DynamicIntegerQuery("data.field3", 16),
                    DynamicIntegerQuery("data.field4", listOf(17, 18)),
                ),
                dynamicBooleanQuery = listOf(
                    DynamicBooleanQuery("data.field5", true),
                ),
                countQuery = CountQuery(19),
            )

            query.getQueryParameters() shouldContainExactlyInAnyOrder listOf(
                QueryParameter("organization", "org1"),
                QueryParameter("user", "s1|v1"),
                QueryParameter("device", "s2|v2"),
                QueryParameter("dataType", "s3|v3"),
                QueryParameter("_id", "i4"),
                QueryParameter("timestamp", "2021-06-07T12:13:14.000Z"),
                QueryParameter("sequenceNumber", "5"),
                QueryParameter("relatedUser", "s6|v6"),
                QueryParameter("relatedPeripheral", "s7|v7"),
                QueryParameter("proposition", "p8"),
                QueryParameter("application", "a9"),
                QueryParameter("subscription", "s10"),
                QueryParameter("dataCategory", "dc11"),
                QueryParameter("dataSource", "ds12"),
                QueryParameter("data.field1:exact", "f13"),
                QueryParameter("data.field2:exact", "f14,f15"),
                QueryParameter("data.field3", "16"),
                QueryParameter("data.field4", "17,18"),
                QueryParameter("data.field5", "true"),
                QueryParameter("_count", "19"),
            )
        }
    }
}