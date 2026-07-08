package org.clockworx.battlearena.storage;

import java.io.File;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.clockworx.battlearena.BattleArena;
import org.clockworx.data.DatabaseSettings;
import org.clockworx.data.DatabaseType;

/**
 * Builds {@link DatabaseSettings} from BattleArena's {@code storage.*} config
 * sections for use with Flyway migrations and Hibernate.
 */
public final class DatabaseSettingsBuilder {

    private static final String DEFAULT_MYSQL_PREFIX = "ba_";
    private static final String DEFAULT_SQLITE_PREFIX = "";

    private DatabaseSettingsBuilder() {
    }

    /**
     * Builds connection settings for the given storage type.
     *
     * @param plugin the plugin instance
     * @param type   the configured storage backend (sqlite or mysql)
     * @return settings for the clockworx-data layer
     * @throws StorageException if required configuration is missing
     */
    public static DatabaseSettings fromConfig(BattleArena plugin, StorageManager.StorageType type) {
        FileConfiguration config = plugin.getConfig();

        return switch (type) {
            case SQLITE -> buildSqliteSettings(plugin, config);
            case MYSQL -> buildMysqlSettings(config);
            default -> throw new StorageException("Database settings are not supported for storage type: " + type);
        };
    }

    private static DatabaseSettings buildSqliteSettings(BattleArena plugin, FileConfiguration config) {
        String dbPath = config.getString("storage.sqlite.file", "data/battlearena.db");
        File databaseFile = new File(plugin.getDataFolder(), dbPath);
        File parentDir = databaseFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        String url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
        String prefix = resolvePrefix(config.getString("storage.sqlite.prefix"), DEFAULT_SQLITE_PREFIX);

        return DatabaseSettings.withDefaults(DatabaseType.SQLITE, url, "", "", prefix);
    }

    private static DatabaseSettings buildMysqlSettings(FileConfiguration config) {
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
        String prefix = resolvePrefix(mysqlConfig.getString("prefix"), DEFAULT_MYSQL_PREFIX);

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useSSL=false&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8";

        return new DatabaseSettings(
                DatabaseType.MYSQL,
                url,
                username,
                password,
                prefix,
                poolSize,
                2,
                300_000L,
                10_000L,
                false,
                "none");
    }

    /**
     * Resolves a configured table prefix, using the default when the key is absent.
     * An explicitly empty string disables prefixing.
     */
    private static String resolvePrefix(String configuredPrefix, String defaultPrefix) {
        if (configuredPrefix == null) {
            return defaultPrefix;
        }
        return configuredPrefix;
    }
}
