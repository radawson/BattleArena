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
