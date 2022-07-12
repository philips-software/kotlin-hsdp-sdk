# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/) 
and this project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased]

## [v0.4.4] - 2022-07-11
### Changed
- Fix timestamp parsing from LastModified header (TDR, CDR)
- In HttpClient.authenticate only try to refresh a token when there is a refresh token (service logins don't have one)
- Upgrade dependencies
### Fixed
- Service login (support PKCS1 private keys)

## [v0.4.3] - 2022-05-02
### Changed
- Upgrade more dependencies

## [v0.4.2] - 2022-05-02
### Changed
- Upgrade dependencies (to fix Dependabot issues)

## [v0.4.1] - 2022-02-07
### Changed
- Make callTimeout leading and set the other timeouts for OkHttpClient to the same value.
- Upgrade dependencies (to fix Dependabot issues)

## [v0.4.0] - 2021-12-08
### Added
- Implement CDR API for batch and transaction operations.
### Changed
- Update dependencies for junit-jupiter-engine and -api, kotest, kover, dokka

## [v0.3.0] - 2021-11-24
### Added
- Implement CDR APIs for read, vread, create, update, patch and delete of FHIR resources.
### Changed
- Increased test timeouts to make unit tests in CI/CD less brittle
- Update dependencies for okHttpVersion, mockk

## [v0.2.0] - 2021-11-17
### Added
- Implement all APIs of IAM User, except for the delegation APIs.
### Changed
- Update dependencies for kotlin, kapt and associated gradle plugins to 1.6.0

## [v0.1.1] - 2021-11-02
### Fixed
- Fix artifact publishing issues to jitpack.io

## [v0.1.0] - 2021-10-29
### Added
- Initial release with IAM OAuth2, IAM User search, TDR, Provisioning identity creation functionality
