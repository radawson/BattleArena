# BattleArena User Guide

## Overview

BattleArena is a comprehensive match and event framework for Minecraft. It allows server administrators to create custom game modes, events, and competitions through configuration files.

## Basic Concepts

### Arenas

An **Arena** represents a game mode configuration. It defines:
- Game rules and mechanics
- Team configurations
- Phases (waiting, active, victory)
- Event actions
- Victory conditions

### Competitions

A **Competition** is an active instance of an Arena. Multiple competitions can run simultaneously for the same Arena.

### Maps

**Maps** are the physical locations where competitions take place. They can be:
- **Static**: Fixed locations that persist
- **Dynamic**: Generated per-competition and cleaned up after

## Player Data Persistence

BattleArena automatically saves your player data when you join competitions. This includes:
- Your inventory and equipment
- Health and hunger levels
- Experience points
- Potion effects
- Your location

When you leave a competition, your data is automatically restored.

### Disconnection Handling

If you disconnect during a competition:
- Your data is saved automatically
- When you reconnect, you can rejoin the competition
- Your saved state will be restored

## Commands

### Backup Commands

- `/ba backups <player>` - View saved player data
- `/ba backup <player>` - Create a manual backup
- `/ba restore <player> <index>` - Restore from backup

### Arena Commands

- `/<arena> join` - Join an arena
- `/<arena> leave` - Leave current arena
- `/<arena> list` - List available competitions

## Storage Backends

Your server administrator configures the storage backend. The system supports:

1. **FlatFile**: Simple file-based storage (default)
2. **SQLite**: Embedded database (better performance)
3. **MySQL**: Network database (for multi-server networks)

As a player, you don't need to worry about which backend is used - your data is handled automatically.

## Tips

1. **Join Competitions**: Use `/arena join` to join available competitions
2. **Check Backups**: Use `/ba backups <yourname>` to see your saved data
3. **Safe Disconnects**: If you need to disconnect, your data is automatically saved
4. **Rejoin**: You can rejoin competitions after disconnecting

## Troubleshooting

### My inventory wasn't restored

- Check if you have a saved backup: `/ba backups <yourname>`
- Contact an administrator if data is missing
- The server may be using a different storage backend

### I can't rejoin after disconnecting

- Try rejoining the competition: `/<arena> join`
- Your saved state should be restored automatically
- If issues persist, contact an administrator

## For Developers

If you're developing plugins that interact with BattleArena:

- Use `BattleArenaApi` for accessing the plugin instance
- Listen to `ArenaEvent` events for game state changes
- Use `StorageManager` for accessing player data persistence
- Check the [Architecture Documentation](ARCHITECTURE.md) for details
