# BattleArena Admin Guide

## Installation

1. Download the latest BattleArena JAR from the releases page
2. Place the JAR in your server's `plugins/` directory
3. Start your server to generate configuration files
4. Configure your arenas and storage settings
5. Restart the server

## Storage Configuration

BattleArena supports three storage backends for player data persistence. Configure the storage system in `config.yml`:

### Storage Type Selection

```yaml
storage:
  type: flatfile  # Options: flatfile, sqlite, mysql
```

### FlatFile Storage (Default)

Best for: Single-server setups, simple deployments

```yaml
storage:
  type: flatfile
  # No additional configuration needed
  # Files are stored in: plugins/BattleArena/saves/players/
```

**Pros:**
- Easy to backup (just copy files)
- Human-readable YAML format
- No external dependencies

**Cons:**
- Slower with many players
- Not suitable for multi-server networks

### SQLite Storage

Best for: Single-server setups requiring better performance

```yaml
storage:
  type: sqlite
  sqlite:
    file: data/battlearena.db  # Relative to plugin data folder
```

**Pros:**
- Fast and efficient
- ACID-compliant transactions
- No external server required
- Better performance than flatfile

**Cons:**
- Not suitable for multi-server networks
- Database file can grow large

### MySQL/MariaDB Storage

Best for: Multi-server networks (BungeeCord/Velocity), large servers

```yaml
storage:
  type: mysql
  mysql:
    host: localhost
    port: 3306
    database: battlearena
    username: minecraft
    password: secret
    pool-size: 10  # Connection pool size (5-10 for small, 10-20 for large servers)
```

**Database Setup:**

1. Create a database:
   ```sql
   CREATE DATABASE battlearena CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. Create a user (optional, for security):
   ```sql
   CREATE USER 'battlearena'@'localhost' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON battlearena.* TO 'battlearena'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. Configure in `config.yml` with your credentials

**Pros:**
- Shared data across multiple servers
- Scalable for large player counts
- External tools can access data
- Better for network setups

**Cons:**
- Requires database server setup
- Network dependency
- More complex configuration

### Persistence Settings

```yaml
storage:
  persist-on-store: true  # Persist data every time PlayerStorage.store() is called
  persist-types: []      # Empty = persist all types
                        # Or specify: [INVENTORY, HEALTH, EXPERIENCE, LOCATION]
```

**Persistence Types:**
- `INVENTORY`: Player inventory and armor
- `GAMEMODE`: Player game mode
- `HEALTH`: Health and hunger
- `EXPERIENCE`: Experience points and levels
- `ATTRIBUTES`: Player attributes (max health, speed, etc.)
- `FLIGHT`: Flight state
- `EFFECTS`: Potion effects
- `LOCATION`: Last known location

## Event System

BattleArena's event system allows you to configure actions that trigger automatically when specific events occur during competitions. Events can be defined at both the arena level (applying to all competitions) and the phase level (applying only during specific phases).

### Quick Start

1. **Define events in your arena configuration file** (`arenas/<arena-name>.yml`)
2. **Choose event types** from the available options (e.g., `on-join`, `on-death`, `on-victory`)
3. **Add actions** that should execute when the event triggers
4. **Test your configuration** by joining a competition

### Basic Example

```yaml
events:
  on-join:
    - store{types=all}
    - change-gamemode{gamemode=adventure}
    - teleport{location=waitroom}
  
  on-leave:
    - clear-effects
    - restore{types=all}
  
  on-death:
    - clear-inventory
    - respawn
    - delay{ticks=20}
    - teleport{location=waitroom}
```

### Setting Up Each Event Type

#### Player Join Events

**Event**: `on-join`  
**When it fires**: When a player joins a competition  
**Common actions**:
- `store{types=all}` - Save player's current state
- `change-gamemode{gamemode=adventure}` - Set game mode
- `teleport{location=waitroom}` - Move to waiting area

**Example**:
```yaml
on-join:
  - store{types=all}
  - change-gamemode{gamemode=adventure}
  - flight{enabled=false}
  - teleport{location=waitroom}
```

#### Player Leave Events

**Event**: `on-leave`  
**When it fires**: When a player leaves a competition  
**Common actions**:
- `clear-effects` - Remove potion effects
- `restore{types=all}` - Restore saved player state

**Example**:
```yaml
on-leave:
  - clear-effects
  - restore{types=all}
```

#### Player Death Events

**Event**: `on-death`  
**When it fires**: When a player dies in a competition  
**Common actions**:
- `clear-inventory` - Remove items
- `respawn` - Respawn the player
- `delay{ticks=20}` - Wait before next action
- `teleport{location=waitroom}` - Move to waiting area

**Example**:
```yaml
on-death:
  - clear-inventory
  - respawn
  - delay{ticks=20}
  - teleport{location=waitroom}
```

#### Player Kill Events

**Event**: `on-kill`  
**When it fires**: When a player kills another player  
**Common actions**:
- `send-message{message=...}` - Notify the killer
- `play-sound{...}` - Play victory sound
- `give-item{item=...}` - Reward the killer

**Example**:
```yaml
on-kill:
  - send-message{message=<green>You killed {killed}!</green>}
  - play-sound{sound=entity.player.levelup;volume=1;pitch=1}
  - give-item{item=golden_apple}
```

#### Phase Start Events

**Event**: `on-start`  
**When it fires**: When a competition phase begins  
**Where to define**: In the `phases:<phase-name>:events:` section  
**Common actions**:
- `broadcast{message=...}` - Announce phase start
- `teleport{location=team_spawn}` - Move players to spawns
- `give-effects{effects=[...]}` - Apply starting effects

**Example**:
```yaml
phases:
  ingame:
    events:
      on-start:
        - broadcast{message=<green>Game starting!</green>;audience=game;type=title}
        - teleport{location=team_spawn}
        - give-effects{effects=[speed{duration=300;amplifier=1}]}
```

#### Victory Events

**Event**: `on-victory`  
**When it fires**: When players win a competition  
**Common actions**:
- `send-message{message=...}` - Congratulate winners
- `play-sound{...}` - Play victory sound
- `run-command{command=...}` - Execute rewards

**Example**:
```yaml
on-victory:
  - send-message{message=<green>Congratulations! You won!</green>}
  - play-sound{sound=entity.player.levelup;volume=1;pitch=1}
  - run-command{command=give {player} diamond 64;source=console}
```

#### Loss Events

**Event**: `on-lose`  
**When it fires**: When players lose a competition  
**Common actions**:
- `send-message{message=...}` - Notify losers
- `play-sound{...}` - Play loss sound

**Example**:
```yaml
on-lose:
  - send-message{message=<red>You lost! Better luck next time.</red>}
  - play-sound{sound=block.anvil.place;volume=1;pitch=0.5}
```

#### Draw Events

**Event**: `on-draw`  
**When it fires**: When a competition ends in a draw  
**Common actions**:
- `broadcast{message=...}` - Announce draw
- `play-sound{...}` - Play draw sound

**Example**:
```yaml
on-draw:
  - broadcast{message=<yellow>It's a draw!</yellow>;audience=game}
  - play-sound{sound=block.beacon.deactivate;volume=1;pitch=1}
```

### Phase-Specific Events

Events can be defined at the phase level to override arena-level events or add phase-specific behavior:

```yaml
phases:
  waiting:
    events:
      on-start:
        - apply-scoreboard{scoreboard=waiting}
      on-join:
        - apply-scoreboard{scoreboard=waiting}
  
  ingame:
    events:
      on-start:
        - equip-class{class=warrior}
        - teleport{location=team_spawn}
        - give-effects{effects=[speed{duration=300;amplifier=1}]}
  
  victory:
    events:
      on-complete:
        - leave
        - restore-arena
      on-victory:
        - send-message{message=<green>You won!</green>}
```

### Using Resolver Placeholders

Resolver placeholders allow dynamic values in messages and commands:

```yaml
on-kill:
  - send-message{message=<green>You killed {killed}!</green>}
  - broadcast{message=<yellow>{killer} eliminated {killed}!</yellow>;audience=game}

on-life-deplete:
  - send-message{message=<yellow>You have {lives-left} lives remaining!</yellow>}
```

See the [Event System Reference](EVENTS.md) for a complete list of available resolvers.

### Action Execution Order

Actions execute sequentially in the order they appear. Use `delay{ticks=...}` to pause execution:

```yaml
on-death:
  - clear-inventory      # Executes immediately
  - respawn              # Executes immediately after
  - delay{ticks=20}      # Waits 1 second (20 ticks)
  - teleport{location=waitroom}  # Executes after delay
```

### Best Practices

1. **Always store on join**: Use `store{types=all}` in `on-join` to save player state
2. **Always restore on leave**: Use `restore{types=all}` in `on-leave` to restore player state
3. **Use delays for respawns**: Add a small delay before teleporting after respawn
4. **Test phase events**: Phase events override arena events, test carefully
5. **Use meaningful messages**: Make use of resolver placeholders for dynamic content

### Troubleshooting Events

**Actions not executing**:
- Verify event name spelling (case-sensitive)
- Check action syntax (parameter names must match exactly)
- Review server logs for parsing errors

**Phase events not working**:
- Ensure phase name matches exactly
- Verify competition is in that phase
- Remember phase events override arena events

**Teleport issues**:
- Ensure team spawns are defined if using `team_spawn`
- Check waitroom/spectator spawns are configured
- Use `join-random-team` before teleporting to team spawns

For complete documentation on all event types and actions, see the [Event System Reference](EVENTS.md).

## Backup Commands

BattleArena provides commands for managing player backups:

### View Backups
```
/ba backups <player>
```
Lists saved player data (in new system, shows current saved state).

### Create Backup
```
/ba backup <player>
```
Manually creates a backup of a player's current state.

### Restore Backup
```
/ba restore <player> <index>
```
Restores player data from a saved backup. In the new system, use index `1` for the current saved state.

## Migration from InventoryBackup

The old `InventoryBackup` system has been replaced with the unified storage system. If you have existing backup files:

1. **Automatic Migration**: The plugin will attempt to load old backups if found
2. **Manual Migration**: Use `/ba backup <player>` to create new format backups
3. **Old Files**: Old backup files in `backups/inventory/` can be safely removed after migration

## Performance Tuning

### For Small Servers (< 50 players)
- Use `flatfile` or `sqlite`
- `pool-size: 5` for MySQL (if used)

### For Medium Servers (50-200 players)
- Use `sqlite` or `mysql`
- `pool-size: 10` for MySQL

### For Large Servers (> 200 players)
- Use `mysql`
- `pool-size: 15-20` for MySQL
- Consider database optimization (indexes, etc.)

## Troubleshooting

### Storage Not Initializing

**Symptoms**: Plugin logs show storage initialization errors

**Solutions**:
1. Check `config.yml` syntax
2. Verify database credentials (for MySQL)
3. Check file permissions (for flatfile/sqlite)
4. Review server logs for detailed error messages

### Data Not Persisting

**Symptoms**: Player data is lost on server restart

**Solutions**:
1. Verify `storage.type` is set correctly
2. Check `persist-on-store: true` in config
3. Ensure storage provider initialized successfully (check startup logs)
4. Verify database connection (for MySQL)

### MySQL Connection Issues

**Symptoms**: "Failed to initialize MySQL storage" errors

**Solutions**:
1. Verify MySQL server is running
2. Check host, port, database name
3. Verify username and password
4. Ensure database exists
5. Check firewall rules
6. Verify user has proper permissions

### Performance Issues

**Symptoms**: Server lag during storage operations

**Solutions**:
1. Storage operations are async, but if issues persist:
2. Switch from `flatfile` to `sqlite` or `mysql`
3. Increase MySQL `pool-size` if using MySQL
4. Check database server performance (for MySQL)
5. Consider disabling `persist-on-store` and only persist on disconnect

## Configuration Examples

### Single Server (Recommended)
```yaml
storage:
  type: sqlite
  sqlite:
    file: data/battlearena.db
  persist-on-store: true
  persist-types: []
```

### Multi-Server Network
```yaml
storage:
  type: mysql
  mysql:
    host: db.example.com
    port: 3306
    database: battlearena
    username: battlearena
    password: secure_password
    pool-size: 15
  persist-on-store: true
  persist-types: []
```

### Development/Testing
```yaml
storage:
  type: flatfile
  persist-on-store: false  # Only persist on disconnect for testing
  persist-types: [INVENTORY, HEALTH, LOCATION]  # Minimal persistence
```

## Best Practices

1. **Regular Backups**: Backup your storage data regularly
   - FlatFile: Copy `saves/players/` directory
   - SQLite: Copy database file
   - MySQL: Use `mysqldump` or your backup tool

2. **Monitor Storage**: Check storage provider status in logs on startup

3. **Test After Changes**: Test storage configuration changes on a test server first

4. **Database Maintenance**: For MySQL, run regular maintenance (optimize tables, etc.)

5. **Security**: Use strong passwords for MySQL, restrict database user permissions
