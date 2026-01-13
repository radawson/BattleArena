package org.clockworx.battlearena.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.clockworx.battlearena.BattleArena;
import org.clockworx.battlearena.storage.PlayerSave;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * MySQL/MariaDB network database storage provider for player data.
 * 
 * This provider uses a MySQL or MariaDB server for storage, making it suitable for:
 * - Multi-server deployments (BungeeCord/Velocity networks)
 * - Large servers with many players
 * - External tools that need database access
 * 
 * Uses HikariCP for efficient connection pooling.
 */
public class MySQLStorageProvider implements StorageProvider {
    
    private final BattleArena plugin;
    private HikariDataSource dataSource;
    private boolean available;
    
    // SQL Statements (MySQL syntax)
    private static final String CREATE_TABLE = """
        CREATE TABLE IF NOT EXISTS player_data (
            uuid VARCHAR(36) PRIMARY KEY,
            player_name VARCHAR(16),
            experience INT,
            health DOUBLE,
            healthp DOUBLE,
            hunger INT,
            magic INT,
            magicp DOUBLE,
            items TEXT,
            match_items TEXT,
            gamemode VARCHAR(16),
            godmode BOOLEAN,
            location TEXT,
            effects TEXT,
            flight BOOLEAN,
            arena_class VARCHAR(64),
            old_team VARCHAR(64),
            scoreboard TEXT,
            money DOUBLE,
            stored_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            INDEX idx_player_name (player_name)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        """;
    
    private static final String INSERT_OR_REPLACE = """
        INSERT INTO player_data 
        (uuid, player_name, experience, health, healthp, hunger, magic, magicp, 
         items, match_items, gamemode, godmode, location, effects, flight, 
         arena_class, old_team, scoreboard, money)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
        player_name = VALUES(player_name),
        experience = VALUES(experience),
        health = VALUES(health),
        healthp = VALUES(healthp),
        hunger = VALUES(hunger),
        magic = VALUES(magic),
        magicp = VALUES(magicp),
        items = VALUES(items),
        match_items = VALUES(match_items),
        gamemode = VALUES(gamemode),
        godmode = VALUES(godmode),
        location = VALUES(location),
        effects = VALUES(effects),
        flight = VALUES(flight),
        arena_class = VALUES(arena_class),
        old_team = VALUES(old_team),
        scoreboard = VALUES(scoreboard),
        money = VALUES(money),
        updated_at = CURRENT_TIMESTAMP
        """;
    
    private static final String SELECT_BY_UUID = "SELECT * FROM player_data WHERE uuid = ?";
    private static final String DELETE_BY_UUID = "DELETE FROM player_data WHERE uuid = ?";
    private static final String EXISTS_BY_UUID = "SELECT 1 FROM player_data WHERE uuid = ? LIMIT 1";
    private static final String SELECT_ALL = "SELECT * FROM player_data";
    
    public MySQLStorageProvider(BattleArena plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "mysql";
    }
    
    @Override
    public CompletableFuture<Void> initialize() {
        return CompletableFuture.runAsync(() -> {
            try {
                FileConfiguration config = plugin.getConfig();
                ConfigurationSection mysqlConfig = config.getConfigurationSection("storage.mysql");
                if (mysqlConfig == null) {
                    throw new StorageException("MySQL configuration not found in config.yml");
                }
                
                String host = mysqlConfig.getString("host", "localhost");
                int port = mysqlConfig.getInt("port", 3306);
                String database = mysqlConfig.getString("database", "battlearena");
                String username = mysqlConfig.getString("username", "minecraft");
                String password = mysqlConfig.getString("password", "secret");
                int poolSize = mysqlConfig.getInt("pool-size", 10);
                
                // Configure HikariCP
                HikariConfig hikariConfig = new HikariConfig();
                hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + 
                    "?useSSL=false&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8");
                hikariConfig.setUsername(username);
                hikariConfig.setPassword(password);
                hikariConfig.setMaximumPoolSize(poolSize);
                hikariConfig.setMinimumIdle(2);
                hikariConfig.setIdleTimeout(300000); // 5 minutes
                hikariConfig.setConnectionTimeout(10000); // 10 seconds
                hikariConfig.setMaxLifetime(1800000); // 30 minutes
                hikariConfig.setPoolName("BattleArena-MySQL-Pool");
                
                // Performance optimizations
                hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
                hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
                hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
                
                // Create connection pool
                dataSource = new HikariDataSource(hikariConfig);
                
                // Create tables
                createTables();
                
                available = true;
                plugin.info("[BattleArena] MySQL storage initialized: " + host + ":" + port + "/" + database);
            } catch (Exception e) {
                plugin.error("Failed to initialize MySQL storage: " + e.getMessage(), e);
                available = false;
                throw new StorageException("Failed to initialize MySQL storage", e);
            }
        });
    }
    
    @Override
    public CompletableFuture<Void> shutdown() {
        return CompletableFuture.runAsync(() -> {
            if (dataSource != null && !dataSource.isClosed()) {
                dataSource.close();
                available = false;
                plugin.info("[BattleArena] MySQL connection pool closed");
            }
        });
    }
    
    @Override
    public boolean isAvailable() {
        return available && dataSource != null && !dataSource.isClosed();
    }
    
    private Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource not initialized");
        }
        return dataSource.getConnection();
    }
    
    private void createTables() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_TABLE);
        }
    }
    
    @Override
    public CompletableFuture<Void> savePlayerData(PlayerSave playerSave) {
        return CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> data = playerSave.toMap();
                
                try (Connection conn = getConnection();
                     PreparedStatement stmt = conn.prepareStatement(INSERT_OR_REPLACE)) {
                    stmt.setString(1, playerSave.getID().toString());
                    stmt.setString(2, playerSave.getName());
                    stmt.setObject(3, data.get("experience"));
                    stmt.setObject(4, data.get("health"));
                    stmt.setObject(5, data.get("healthp"));
                    stmt.setObject(6, data.get("hunger"));
                    stmt.setObject(7, data.get("magic"));
                    stmt.setObject(8, data.get("magicp"));
                    stmt.setString(9, serializeMap(data.get("items")));
                    stmt.setString(10, serializeMap(data.get("matchItems")));
                    stmt.setString(11, data.get("gamemode") != null ? data.get("gamemode").toString() : null);
                    stmt.setObject(12, data.get("godmode"));
                    stmt.setString(13, data.get("location") != null ? data.get("location").toString() : null);
                    stmt.setString(14, serializeList(data.get("effects")));
                    stmt.setObject(15, data.get("flight"));
                    stmt.setString(16, data.get("arenaClass") != null ? data.get("arenaClass").toString() : null);
                    stmt.setString(17, data.get("oldTeam") != null ? data.get("oldTeam").toString() : null);
                    stmt.setString(18, null); // scoreboard - not serialized yet
                    stmt.setObject(19, data.get("money"));
                    
                    stmt.executeUpdate();
                }
            } catch (Exception e) {
                plugin.error("Failed to save player data for " + playerSave.getID() + ": " + e.getMessage(), e);
                throw new StorageException("Failed to save player data", e);
            }
        });
    }
    
    @Override
    public CompletableFuture<Optional<PlayerSave>> loadPlayerData(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                try (Connection conn = getConnection();
                     PreparedStatement stmt = conn.prepareStatement(SELECT_BY_UUID)) {
                    stmt.setString(1, uuid.toString());
                    
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (!rs.next()) {
                            return Optional.empty();
                        }
                        
                        // TODO: Properly integrate with new codebase's ArenaPlayer
                        // For now, create a minimal PlayerSave
                        // This needs to be adapted to work with the new codebase structure
                        String playerName = rs.getString("name");
                        if (playerName == null) playerName = "Unknown";
                        PlayerSave playerSave = new PlayerSave(uuid, playerName);
                        Map<String, Object> data = new java.util.HashMap<String, Object>();
                        
                        data.put("experience", rs.getObject("experience"));
                        data.put("health", rs.getObject("health"));
                        data.put("healthp", rs.getObject("healthp"));
                        data.put("hunger", rs.getObject("hunger"));
                        data.put("magic", rs.getObject("magic"));
                        data.put("magicp", rs.getObject("magicp"));
                        data.put("items", deserializeMap(rs.getString("items")));
                        data.put("matchItems", deserializeMap(rs.getString("match_items")));
                        String gm = rs.getString("gamemode");
                        if (gm != null) data.put("gamemode", gm);
                        data.put("godmode", rs.getObject("godmode"));
                        String loc = rs.getString("location");
                        if (loc != null) data.put("location", loc);
                        data.put("effects", deserializeList(rs.getString("effects")));
                        data.put("flight", rs.getObject("flight"));
                        String ac = rs.getString("arena_class");
                        if (ac != null) data.put("arenaClass", ac);
                        String ot = rs.getString("old_team");
                        if (ot != null) data.put("oldTeam", ot);
                        data.put("money", rs.getObject("money"));
                        
                        playerSave.fromMap(data);
                        return Optional.of(playerSave);
                    }
                }
            } catch (Exception e) {
                plugin.error("Failed to load player data for " + uuid + ": " + e.getMessage(), e);
                return Optional.empty();
            }
        });
    }
    
    @Override
    public CompletableFuture<Boolean> deletePlayerData(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                try (Connection conn = getConnection();
                     PreparedStatement stmt = conn.prepareStatement(DELETE_BY_UUID)) {
                    stmt.setString(1, uuid.toString());
                    int rows = stmt.executeUpdate();
                    return rows > 0;
                }
            } catch (Exception e) {
                plugin.error("Failed to delete player data for " + uuid + ": " + e.getMessage(), e);
                return false;
            }
        });
    }
    
    @Override
    public CompletableFuture<Boolean> playerDataExists(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                try (Connection conn = getConnection();
                     PreparedStatement stmt = conn.prepareStatement(EXISTS_BY_UUID)) {
                    stmt.setString(1, uuid.toString());
                    try (ResultSet rs = stmt.executeQuery()) {
                        return rs.next();
                    }
                }
            } catch (Exception e) {
                plugin.error("Failed to check if player data exists for " + uuid + ": " + e.getMessage(), e);
                return false;
            }
        });
    }
    
    @Override
    public CompletableFuture<List<PlayerSave>> loadAllPlayerData() {
        return CompletableFuture.supplyAsync(() -> {
            List<PlayerSave> playerSaves = new ArrayList<PlayerSave>();
            try {
                try (Connection conn = getConnection();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
                    
                    while (rs.next()) {
                        try {
                            UUID uuid = UUID.fromString(rs.getString("uuid"));
                            Optional<PlayerSave> saveOpt = loadPlayerData(uuid).join();
                            if (saveOpt.isPresent()) {
                                playerSaves.add(saveOpt.get());
                            }
                        } catch (Exception e) {
                            plugin.error("Failed to load player data from result set: " + e.getMessage(), e);
                        }
                    }
                }
            } catch (Exception e) {
                plugin.error("Failed to load all player data: " + e.getMessage(), e);
            }
            return playerSaves;
        });
    }
    
    // Helper methods for serialization (same as SQLite)
    private String serializeMap(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) obj;
            StringBuilder sb = new StringBuilder();
            if (map.containsKey("armor")) {
                sb.append("armor:").append(serializeList(map.get("armor"))).append(";");
            }
            if (map.containsKey("contents")) {
                sb.append("contents:").append(serializeList(map.get("contents"))).append(";");
            }
            return sb.toString();
        }
        return obj.toString();
    }
    
    private Object deserializeMap(String str) {
        if (str == null || str.isEmpty()) return null;
        Map<String, Object> map = new java.util.HashMap<String, Object>();
        String[] parts = str.split(";");
        for (String part : parts) {
            if (part.contains(":")) {
                String[] keyValue = part.split(":", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0];
                    String value = keyValue[1];
                    if ("armor".equals(key) || "contents".equals(key)) {
                        map.put(key, deserializeList(value));
                    }
                }
            }
        }
        return map.isEmpty() ? null : map;
    }
    
    private String serializeList(Object obj) {
        if (obj == null) return "";
        if (obj instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> list = (List<String>) obj;
            return String.join("|", list);
        }
        return obj.toString();
    }
    
    private Object deserializeList(String str) {
        if (str == null || str.isEmpty()) return new ArrayList<String>();
        List<String> list = new ArrayList<String>();
        for (String item : str.split("\\|")) {
            if (!item.trim().isEmpty()) {
                list.add(item.trim());
            }
        }
        return list;
    }
}
