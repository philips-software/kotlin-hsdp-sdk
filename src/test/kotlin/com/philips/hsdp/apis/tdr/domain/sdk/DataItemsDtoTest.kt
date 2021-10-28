/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk

import com.philips.hsdp.apis.tdr.domain.conversion.defaultLimit
import com.philips.hsdp.apis.tdr.domain.conversion.toDataItemsDto
import com.philips.hsdp.apis.tdr.domain.hsdp.*
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonObject

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import kotlin.math.min

internal class DataItemsDtoTest {

    private fun generateBundle(total: Int, offset: Int, limit: Int? = null): Bundle {
        val actualLimit = limit?: defaultLimit
        return Bundle(
            resourceType = ResourceType.Bundle,
            type = Type.SearchSet,
            total = total,
            _startAt = offset,
            link = listOf(Link(Relation.Next, "blabla&_startAt=${min(offset + actualLimit, total)}${if (limit != null) "&_count=$limit" else ""}")),
            entry = List(total) {
                BundleEntry(
                    fullUrl = "data-item-$it",
                    resource = DataItem(
                        id = "data-item-$it",
                        meta = Meta(
                            lastUpdated = "2021-09-27T12:13:14.150Z",
                            versionId = "$it",
                        ),
                        timestamp = "2021-09-27T12:13:14.150Z",
                        user = Identifier(
                            system = "",
                            value = "SomeUser",
                        ),
                        dataType = Coding(
                            system = "Tenant",
                            code = "Code-$it",
                        ),
                        organization = "Org1",
                        data = JsonObject(emptyMap()),
                        deleteTimestamp = "2021-10-27T12:13:14.150Z",
                        creationTimestamp = "2021-09-27T12:13:14.150Z",
                    ),
                )
            },
        )
    }

    data class Input(val total: Int, val offset: Int, val limit: Int? = null)
    data class Expected(val size: Int, val offset: Int, val limit: Int)

    @Nested
    inner class FromBundle {
        @TestFactory
        fun `Receiving a bundle with the _count query parameter in the url`() = listOf(
            // Bundles with _count in the link url
            Input(10, 0, 10) to Expected(10, 0, 10),
            Input(8, 10, 10) to Expected(8, 10, 10),
            // Bundles without _count in the link url
            Input(100, 0, null) to Expected(100, 0, defaultLimit),
            Input(80, 100, null) to Expected(80, 100, defaultLimit),
        ).flatMap { (input, expected) ->
            with(input) {
                val bundle = generateBundle(total, offset, limit)
                val dataItems = bundle.toDataItemsDto("request-id")
                listOf(
                    DynamicTest.dynamicTest("Should generate page with ${expected.size} contracts and links when bundle has [total,offset,limit]=[$total,$offset,$limit]") {
                        dataItems.data.size shouldBe expected.size
                    },
                    DynamicTest.dynamicTest("Should generate page with pagination offset ${expected.offset} when bundle has [total,offset,limit]=[$total,$offset,$limit]") {
                        dataItems.pagination shouldBe PaginationDto(expected.offset, expected.limit)
                    },
                    DynamicTest.dynamicTest("Should generate contracts with self link equal to 'data-item-[index] when bundle has [total,offset,limit]=[$total,$offset,$limit]") {
                        dataItems.data.map { it.link?.self } shouldContainExactly (bundle.entry.mapIndexed { i, _ -> "data-item-$i"})
                    },
                    DynamicTest.dynamicTest("Should generate page with request-id") {
                        dataItems.requestId shouldBe "request-id"
                    },
                )
            }
        }
    }
}
