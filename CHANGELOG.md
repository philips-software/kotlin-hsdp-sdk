# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/) 
and this project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased]

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
