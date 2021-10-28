/**
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
package com.philips.hsdp.apis.tdr.domain.sdk.query

import com.philips.hsdp.apis.support.ValidationPatterns.iso8601UtcPlusOperatorPattern
import com.thinkinglogic.builder.annotation.Builder

/**
 * Query structure for getting data items.
 *
 * In HSDP TDR API, all query parameters are part of the URL. They are sometimes also interrelated, and often optional.
 * The SDK abstracts this for the user into query data structures, performing checks on those structures, and assembles
 * the URL to be used in the call to HSDP TDR API.
 */
@Builder
data class DataItemQuery(
    /**
     * Mandatory query that allows for filtering on certain organization.
     */
    val organizationQuery: OrganizationQuery,
    /**
     * Optional query that allows for filtering on the data item user.
     */
    val userQuery: UserQuery? = null,
    /**
     * Optional query that allows for filtering on the data item device.
     */
    val deviceQuery: DeviceQuery? = null,
    /**
     * Optional query that allows for filtering on certain data type.
     */
    val dataTypeQuery: DataTypeQuery? = null,
    /**
     * Optional query that allows for filtering on certain data item IDs.
     */
    val idQuery: DataItemIdQuery? = null,
    /**
     * Optional query that allows for filtering on certain data item timestamp.
     */
    val timestampQuery: TimestampQuery? = null,
    /**
     * Optional query that allows for filtering on certain data item sequence number.
     */
    val sequenceNumberQuery: SequenceNumberQuery? = null,
    /**
     * Optional query that allows for filtering on certain data item related user.
     */
    val relatedUserQuery: RelatedUserQuery? = null,
    /**
     * Optional query that allows for filtering on certain data item related peripheral.
     */
    val relatedPeripheralQuery: RelatedPeripheralQuery? = null,
    /**
     * Optional query that allows for filtering on certain data item proposition.
     */
    val propositionQuery: PropositionQuery? = null,
    /**
     * Optional query that allows for filtering on certain data item application.
     */
    val applicationQuery: ApplicationQuery? = null,
    /**
     * Optional query that allows for filtering on certain data item subscription.
     */
    val subscriptionQuery: SubscriptionQuery? = null,
    /**
     * Optional query that allows for filtering on certain data item data category.
     */
    val dataCategoryQuery: DataCategoryQuery? = null,
    /**
     * Optional query that allows for filtering on certain data item data source.
     */
    val dataSourceQuery: DataSourceQuery? = null,
    /**
     * Optional query that allows for filtering on certain data item dynamic string parameter.
     *
     * If used, the [dataTypeQuery] must also be present.
     */
    val dynamicStringQuery: List<DynamicStringQuery>? = null,
    /**
     * Optional query that allows for filtering on certain data item dynamic integer parameter.
     *
     * If used, the [dataTypeQuery] must also be present.
     */
    val dynamicIntegerQuery: List<DynamicIntegerQuery>? = null,
    /**
     * Optional query that allows for filtering on certain data item dynamic boolean parameter.
     *
     * If used, the [dataTypeQuery] must also be present.
     */
    val dynamicBooleanQuery: List<DynamicBooleanQuery>? = null,
    /**
     * Optional query that allows for indicating the number of results to receive on a 'page'.
     */
    val countQuery: CountQuery? = null,
    /**
     * Optional query that allows for indicating which 'page' to receive.
     */
    val offsetQuery: OffsetQuery? = null,
) {
    init {
//        // NOTE: the API spec says this is required, but TDR does not need it
//        require(userQuery != null || deviceQuery != null) {
//            "Either a user or device query must be supplied"
//        }
        val dynamicPresent = dynamicStringQuery?.isNotEmpty() ?: false ||
                dynamicIntegerQuery?.isNotEmpty() ?: false ||
                dynamicBooleanQuery?.isNotEmpty() ?: false
        require((dynamicPresent && dataTypeQuery != null) || !dynamicPresent) {
            "Dynamic queries require the datatype to be specified as well"
        }
    }

    /**
     * Convert the query to a list of query parameter that can be used to construct the query parameters of a URL.
     */
    fun getQueryParameters(): List<QueryParameter> {
        return listOfNotNull(
            organizationQuery.asQueryParameter(),
            userQuery?.asQueryParameter(),
            deviceQuery?.asQueryParameter(),
            dataTypeQuery?.asQueryParameter(),
            idQuery?.asQueryParameter(),
            timestampQuery?.asQueryParameter(),
            sequenceNumberQuery?.asQueryParameter(),
            relatedUserQuery?.asQueryParameter(),
            relatedPeripheralQuery?.asQueryParameter(),
            propositionQuery?.asQueryParameter(),
            applicationQuery?.asQueryParameter(),
            subscriptionQuery?.asQueryParameter(),
            dataCategoryQuery?.asQueryParameter(),
            dataSourceQuery?.asQueryParameter(),
            countQuery?.asQueryParameter(),
            offsetQuery?.asQueryParameter(),
        ) +
            (dynamicStringQuery?.map { it.asQueryParameter() } ?: emptyList()) +
            (dynamicIntegerQuery?.map { it.asQueryParameter() } ?: emptyList()) +
            (dynamicBooleanQuery?.map { it.asQueryParameter() } ?: emptyList())
    }
}

/**
 * Interface for dynamic queries, to support queries on the 'data' field in a data item.
 */
interface DynamicQuery {
    val name: String

    /**
     * Validation for dynamic queries.
     *
     * Dynamic queries can only query inside the 'data' field of a data item.
     */
    fun validateDynamic() {
        require(name.startsWith("data.")) {
            "Dynamic queries must prefix data fields with 'data.'"
        }
    }
}

/**
 * Query structure for the user of a data item.
 */
data class UserQuery(
    override val system: List<String>,
    override val value: List<String>,
): SystemCodeStringQuery {
    override val name = "user"

    constructor(system: String, value: String): this(listOf(system), listOf(value))

    init  {
        validate()
    }
}

/**
 * Query structure for the device of a data item.
 */
data class DeviceQuery(
    override val system: List<String>,
    override val value: List<String>,
): SystemCodeStringQuery {
    override val name = "device"

    constructor(system: String, value: String): this(listOf(system), listOf(value))

    init  {
        validate()
    }
}

/**
 * Query structure for the data type of a data item.
 */
data class DataTypeQuery(
    override val system: List<String>,
    override val value: List<String>,
): SystemCodeStringQuery {
    override val name = "dataType"

    constructor(system: String, value: String): this(listOf(system), listOf(value))

    init {
        validate()
        require(system.size == 1 && value.size == 1) {
            "The supplied system and value lists must each consist of a single item"
        }
    }
}

/**
 * Query structure for the ID of a data item.
 */
data class DataItemIdQuery(
    override val value: List<String>,
): StringQuery {
    override val name = "_id"

    constructor(value: String): this(listOf(value))

    init {
        validate()
    }
}

/**
 * Query structure for the timestamp of a data item.
 *
 * The supplied timestamps should be in ISO-8601 format, and may be prefixed with one of the following operations:
 * eq (equal), ne (not equal), le (less than or equal), ge (greater than or equal), lt (less than), gt (greater than)
 */
data class TimestampQuery(
    override val value: List<String>,
): StringQuery {
    override val name = "timestamp"

    constructor(value: String): this(listOf(value))

    init {
        super.validate()
        require(value.all { it.matches(iso8601UtcPlusOperatorPattern) }) {
            "Timestamp not according to ISO8601 ISO format with optional operator prefix"
        }
    }
}

/**
 * Query structure for the related user of a data item.
 */
data class RelatedUserQuery(
    override val system: List<String>,
    override val value: List<String>,
): SystemCodeStringQuery {
    override val name = "relatedUser"

    constructor(system: String, value: String): this(listOf(system), listOf(value))

    init {
        validate()
    }
}

/**
 * Query structure for the related peripheral of a data item.
 */
data class RelatedPeripheralQuery(
    override val system: List<String>,
    override val value: List<String>,
): SystemCodeStringQuery {
    override val name = "relatedPeripheral"

    constructor(system: String, value: String): this(listOf(system), listOf(value))

    init {
        validate()
    }
}

/**
 * Query structure for the sequence number of a data item.
 */
data class SequenceNumberQuery(
    override val value: List<Int>,
): IntegerQuery {
    override val name = "sequenceNumber"

    constructor(value: Int): this(listOf(value))

    init {
        validate()
    }
}

/**
 * Query structure for the proposition of a data item.
 */
data class PropositionQuery(
    override val value: List<String>,
): StringQuery {
    override val name = "proposition"

    constructor(value: String): this(listOf(value))

    init {
        validate()
    }
}

/**
 * Query structure for the application of a data item.
 */
data class ApplicationQuery(
    override val value: List<String>,
): StringQuery {
    override val name = "application"

    constructor(value: String): this(listOf(value))

    init {
        validate()
    }
}

/**
 * Query structure for the subscription of a data item.
 */
data class SubscriptionQuery(
    override val value: List<String>,
): StringQuery {
    override val name = "subscription"

    constructor(value: String): this(listOf(value))

    init {
        validate()
    }
}

/**
 * Query structure for the data category of a data item.
 */
data class DataCategoryQuery(
    override val value: List<String>,
): StringQuery {
    override val name = "dataCategory"

    constructor(value: String): this(listOf(value))

    init {
        validate()
    }
}

/**
 * Query structure for the data source of a data item.
 */
data class DataSourceQuery(
    override val value: List<String>,
): StringQuery {
    override val name = "dataSource"

    constructor(value: String): this(listOf(value))

    init {
        validate()
    }
}

/**
 * Query structure for dynamic string parameters of a data item.
 *
 * The name of a dynamic string parameter must start with "data." and end with ":exact".
 */
data class DynamicStringQuery(
    override val name: String,
    override val value: List<String>,
): StringQuery, DynamicQuery {

    constructor(name: String, value: String): this(name, listOf(value))

    init {
        validate()
        validateDynamic()
        require(name.endsWith(":exact")) {
            "Dynamic string query parameter names must end with ':exact'"
        }
    }
}

/**
 * Query structure for dynamic integer parameters of a data item.
 *
 * The name of a dynamic string parameter must start with "data.".
 */
data class DynamicIntegerQuery(
    override val name: String,
    override val value: List<Int>,
): IntegerQuery, DynamicQuery {

    constructor(name: String, value: Int): this(name, listOf(value))

    init {
        validate()
        validateDynamic()
    }
}

/**
 * Query structure for dynamic boolean parameters of a data item.
 *
 * The name of a dynamic string parameter must start with "data.".
 */
data class DynamicBooleanQuery(
    override val name: String,
    override val value: Boolean,
): BooleanQuery, DynamicQuery {
    init {
        validateDynamic()
    }
}
