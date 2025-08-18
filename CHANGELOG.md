# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
- Context information provider from local sensors.

## [0.2.0] - 2021-03-07
### Added
- Lua transcoder for building BLE drivers.
- SensorTag driver in Lua.
- Rest communication for the drivers server.

### Changed
- Manage of technologies in the M-Hub builder.
- Move visibility of most of the compononents as internal.

### Fixed
- Fix BLE technology to support dynamic drivers

## [0.1.0] - 2020-10-30
### Added
- Basic architecture of the mobile hub.
- Support for multiple WPAN, WLAN and CEP technologies.
- Initial implementation of BLE as WPAN technology with SensorTag driver.
- MQTT and MRUDP implementations of WLAN techonology.
- Asper implementation of CEP technology.

[Unreleased]: https://github.com/Luistlvr1989/MobileHub/compare/v0.1.0...HEAD
[0.2.0]: https://github.com/Luistlvr1989/MobileHub/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/Luistlvr1989/MobileHub/releases/tag/v0.1.0