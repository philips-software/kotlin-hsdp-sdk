# Module kotlin-hsdp-api

The HSDP SDK, written in Kotlin, can be used in Java/Kotlin backend services and Android applications to access HSDP 
services in a user-friendly way. It abstracts away details that are of no interest to an application developer.
Whereas HSDP services always return bundles with bundle entries that contain resources, an application developer is 
only interested in sending/receiving simple domain objects, like users/contracts/data items. The SDK removes the 
clutter and just returns the meat. 

# Package com.philips.hsdp.apis.iam

Contains all functionality related to the HSDP Identity and Access Management.
The IAM service of HSDP has a lot of functionality, split over submodules.

# Package com.philips.hsdp.apis.tdr

Contains all functionality related to the HSDP Telemetry Data Repository.

# package com.philips.hsdp.apis.tdr.domain

Contains the domain models for both the HSDP SDK and the HSDP API, and conversions that will translate from 
one domain to the other.

# Package com.philips.hsdp.apis.tdr.domain.hsdp

Contains all domain models that are used in the HSDP API.

# Package com.philips.hsdp.apis.tdr.domain.sdk

Contains all domain models that are used in the HSDP SDK.

# Package com.philips.hsdp.apis.tdr.domain.conversion

Contains all conversion functions between both domains.

# Package com.philips.hsdp.apis.provisioning

Contains all functionality related to the HSDP Provisioning Service.

# Package com.philips.hsdp.apis.provisioning.domain

Contains the domain models for both the HSDP SDK and the HSDP API, and conversions that will translate from
one domain to the other.

# Package com.philips.hsdp.apis.provisioning.domain.hsdp

Contains all domain models that are used in the HSDP API.

# Package com.philips.hsdp.apis.provisioning.domain.sdk

Contains all domain models that are used in the HSDP SDK.

# Package com.philips.hsdp.apis.provisioning.domain.conversion

Contains all conversion functions between both domains.

# Package com.philips.hsdp.apis.support

Contains some common functionality that is used in the SDK services.
