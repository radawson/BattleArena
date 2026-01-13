# BattleArena Documentation

Welcome to the BattleArena documentation! BattleArena is a complete match and event framework for Minecraft, built on Paper.

## Documentation Index

### [Architecture Guide](ARCHITECTURE.md)
Learn about BattleArena's internal architecture, storage system, and how components interact.

### [Admin Guide](ADMIN_GUIDE.md)
Complete guide for server administrators covering installation, configuration, storage backends, and troubleshooting.

### [User Guide](USER_GUIDE.md)
Player-facing guide explaining how to use BattleArena features and commands.

### [Changelog](CHANGELOG.md)
Version history and release notes.

## Quick Start

1. **Installation**: Place the JAR in your `plugins/` directory
2. **Configuration**: Edit `config.yml` to configure storage and arenas
3. **Storage**: Choose your storage backend (flatfile, sqlite, or mysql)
4. **Arenas**: Create arena configurations in the `arenas/` directory
5. **Start**: Restart your server and start playing!

## Key Features

- **Multiple Storage Backends**: FlatFile, SQLite, and MySQL support
- **Real-time Persistence**: Automatic player data saving
- **Paper API**: Full integration with Paper's modern APIs
- **Event-Driven**: Flexible event system for custom game modes
- **Modular**: Extensible module system for custom functionality

## Storage System

BattleArena includes a comprehensive storage system that automatically persists player data:

- **Automatic Saving**: Data is saved when players join competitions
- **Multiple Backends**: Choose the storage solution that fits your needs
- **Async Operations**: Non-blocking storage operations for better performance
- **Disconnect Handling**: Automatic data persistence on player disconnect

See the [Admin Guide](ADMIN_GUIDE.md) for detailed storage configuration.

## Support

- **GitHub**: [https://github.com/clockworx/battlearena](https://github.com/clockworx/battlearena)
- **Issues**: Report bugs and request features on GitHub
- **Documentation**: This documentation is continuously updated

## Version Information

- **Current Version**: 5.0.0
- **Minecraft Version**: 1.21.11
- **Paper API**: Required
- **Java Version**: 17+

## Recent Changes

### Version 5.0.0
- Complete storage system overhaul
- Paper-only plugin (no Spigot compatibility)
- Package migration to `org.clockworx.battlearena`
- Brigadier command system integration

See [CHANGELOG.md](CHANGELOG.md) for full details.
