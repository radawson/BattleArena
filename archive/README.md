# Archived Codebase

This directory contains the old BattleArena codebase that was migrated from `src/java/mc/alk/arena/` to the new structure.

## Archive Date
January 13, 2025

## What Was Archived

- **Old Source Code**: `src/java/mc/alk/arena/` - The original BattleArena codebase using `mc.alk.arena` packages
- **Old Configuration Files**: `src/java/plugin.yml` and `src/java/config.yml` (if they differed from the new versions)

## Migration Context

The old codebase was archived as part of a major refactoring to:
1. Consolidate to a single codebase structure (removing dual codebase confusion)
2. Migrate packages from `org.battleplugins.arena` to `org.clockworx.battlearena`
3. Modernize to Paper-native APIs (Brigadier commands instead of NMS)
4. Align with Paper's recommended project structure

## Important Notes

- **Storage System**: The storage system classes (`StorageManager`, `StorageProvider`, etc.) were migrated to the new codebase at `plugin/src/main/java/org/clockworx/battlearena/storage/`
- **PlayerSave**: The `PlayerSave` class was migrated and adapted for the new codebase
- **Commands**: The old command system using NMS reflection was replaced with Paper's native Brigadier API

## Do Not Use

This archived codebase should **NOT** be used for new development. All active development should use the new codebase structure in `plugin/src/main/java/org/clockworx/battlearena/`.

This archive is kept for reference purposes only.
