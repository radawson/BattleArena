# BattleArena Changelog

## [5.0.0] - 2025-01-13

### Major Changes
- **Converted to Paper-Only Plugin**: Full migration from Spigot to Paper API
- **Package Migration**: Changed base package from `org.battleplugins` to `org.clockworx.battlearena`
- **Command System**: Migrated to Paper's native Brigadier Command API

### Storage System Overhaul
- **New Unified Storage System**: Replaced `InventoryBackup` with a comprehensive storage solution
- **Multiple Storage Backends**: Added support for FlatFile (YAML), SQLite, and MySQL/MariaDB
- **Real-time Persistence**: Player data is now persisted automatically when `PlayerStorage.store()` is called
- **StorageAdapter**: New bridge layer between in-memory `PlayerStorage` and persistent storage
- **Enhanced PlayerSave**: Complete player data serialization including inventory, attributes, effects, location, and more
- **Paper API Integration**: Uses Paper's `ItemStack.serializeAsBytes()` for optimal inventory serialization
- **Async Operations**: All storage operations are asynchronous to prevent main thread blocking

### Technical Improvements
- **Gradle 9.2.1**: Updated build system to latest stable Gradle version
- **Minecraft 1.21.11**: Full compatibility with latest Minecraft version
- **Deprecated API Updates**: Replaced deprecated `Attribute.name()` and `Attribute.valueOf()` with modern alternatives

### Breaking Changes
- **InventoryBackup Deprecated**: The old `InventoryBackup` system is deprecated. Use the new storage system instead.
- **Package Changes**: All classes moved from `org.battleplugins` to `org.clockworx.battlearena`

### Configuration Changes
- Added `storage` section to `config.yml` with options for:
  - Storage type selection (flatfile, sqlite, mysql)
  - SQLite database configuration
  - MySQL connection settings
  - Persistence behavior configuration

## [4.0.5] - Previous Version
- Add SQLite and MySQL storage support
