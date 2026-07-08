package org.clockworx.battlearena.storage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import org.bukkit.plugin.IllegalPluginAccessException;
import org.clockworx.battlearena.BattleArena;
import org.clockworx.battlearena.entity.PlayerDataEntity;
import org.clockworx.data.DatabaseSettings;
import org.clockworx.data.flyway.FlywayMigrator;
import org.clockworx.data.hibernate.HibernateSessionManager;
import org.hibernate.query.Query;

/**
 * Hibernate-backed storage provider for SQLite and MySQL player data.
 * <p>
 * Schema creation is owned by Flyway; persistence uses the shared
 * {@link HibernateSessionManager} from the clockworx-data library.
 */
public class HibernateStorageProvider implements StorageProvider {

    private final BattleArena plugin;
    private final StorageManager.StorageType storageType;
    private final DatabaseSettings settings;
    private final HibernateSessionManager sessions;
    private final Executor asyncExecutor;

    private volatile boolean available;

    /**
     * Creates a provider for the given SQL storage backend.
     *
     * @param plugin      the plugin instance
     * @param storageType sqlite or mysql
     */
    public HibernateStorageProvider(BattleArena plugin, StorageManager.StorageType storageType) {
        this.plugin = plugin;
        this.storageType = storageType;
        this.settings = DatabaseSettingsBuilder.fromConfig(plugin, storageType);
        this.asyncExecutor = createAsyncExecutor(plugin);
        this.sessions = new HibernateSessionManager(
                settings,
                List.of(PlayerDataEntity.class),
                asyncExecutor,
                plugin.getLogger());
    }

    @Override
    public String getName() {
        return storageType.getId();
    }

    @Override
    public CompletableFuture<Void> initialize() {
        return CompletableFuture.runAsync(() -> {
            try {
                FlywayMigrator.migrate(plugin.getClass().getClassLoader(), settings, plugin.getLogger());
                sessions.getSessionFactory();
                available = true;

                if (storageType == StorageManager.StorageType.MYSQL) {
                    plugin.info("[BattleArena] MySQL storage initialized with table prefix '"
                            + settings.tablePrefix() + "'");
                } else {
                    plugin.info("[BattleArena] SQLite storage initialized: " + settings.url());
                }
            } catch (Exception e) {
                available = false;
                plugin.error("Failed to initialize " + storageType.name() + " storage: " + e.getMessage(), e);
                throw new StorageException("Failed to initialize " + storageType.name() + " storage", e);
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<Void> shutdown() {
        return CompletableFuture.runAsync(() -> {
            sessions.shutdown();
            available = false;
            plugin.info("[BattleArena] " + storageType.name() + " storage shut down");
        }, asyncExecutor);
    }

    @Override
    public boolean isAvailable() {
        return available && !sessions.isShuttingDown();
    }

    @Override
    public CompletableFuture<Void> savePlayerData(PlayerSave playerSave) {
        return sessions.executeTransactionVoid(session -> {
            Map<String, Object> data = playerSave.toMap();
            LocalDateTime now = LocalDateTime.now();

            PlayerDataEntity entity = session.get(PlayerDataEntity.class, playerSave.getID().toString());
            if (entity == null) {
                entity = new PlayerDataEntity(playerSave.getID().toString());
                entity.setStoredAt(now);
            }

            entity.setPlayerName(playerSave.getName());
            entity.setExperience(asInteger(data.get("experience")));
            entity.setHealth(asDouble(data.get("health")));
            entity.setHealthp(asDouble(data.get("healthp")));
            entity.setHunger(asInteger(data.get("hunger")));
            entity.setMagic(asInteger(data.get("magic")));
            entity.setMagicp(asDouble(data.get("magicp")));
            entity.setItems(serializeMap(data.get("items")));
            entity.setMatchItems(serializeMap(data.get("matchItems")));
            entity.setGamemode(data.get("gamemode") != null ? data.get("gamemode").toString() : null);
            entity.setGodmode(asBoolean(data.get("godmode")));
            entity.setLocation(data.get("location") != null ? data.get("location").toString() : null);
            entity.setEffects(serializeList(data.get("effects")));
            entity.setFlight(asBoolean(data.get("flight")));
            entity.setArenaClass(data.get("arenaClass") != null ? data.get("arenaClass").toString() : null);
            entity.setOldTeam(data.get("oldTeam") != null ? data.get("oldTeam").toString() : null);
            entity.setScoreboard(null);
            entity.setMoney(asDouble(data.get("money")));
            entity.setUpdatedAt(now);

            session.merge(entity);
        });
    }

    @Override
    public CompletableFuture<Optional<PlayerSave>> loadPlayerData(UUID uuid) {
        return sessions.executeRead(session -> {
            PlayerDataEntity entity = session.get(PlayerDataEntity.class, uuid.toString());
            if (entity == null) {
                return Optional.empty();
            }
            return Optional.of(entityToPlayerSave(entity));
        }, Optional.empty());
    }

    @Override
    public CompletableFuture<Boolean> deletePlayerData(UUID uuid) {
        return sessions.executeTransaction(session -> {
            PlayerDataEntity entity = session.get(PlayerDataEntity.class, uuid.toString());
            if (entity == null) {
                return false;
            }
            session.remove(entity);
            return true;
        });
    }

    @Override
    public CompletableFuture<Boolean> playerDataExists(UUID uuid) {
        return sessions.executeRead(session -> session.get(PlayerDataEntity.class, uuid.toString()) != null, false);
    }

    @Override
    public CompletableFuture<List<PlayerSave>> loadAllPlayerData() {
        return sessions.executeRead(session -> {
            Query<PlayerDataEntity> query = session.createQuery("FROM PlayerDataEntity", PlayerDataEntity.class);
            return query.list().stream()
                    .map(this::entityToPlayerSave)
                    .collect(Collectors.toList());
        }, List.of());
    }

    private PlayerSave entityToPlayerSave(PlayerDataEntity entity) {
        String playerName = entity.getPlayerName();
        if (playerName == null) {
            playerName = "Unknown";
        }

        PlayerSave playerSave = new PlayerSave(UUID.fromString(entity.getUuid()), playerName);
        Map<String, Object> data = new HashMap<>();

        if (entity.getExperience() != null) {
            data.put("experience", entity.getExperience());
        }
        if (entity.getHealth() != null) {
            data.put("health", entity.getHealth());
        }
        if (entity.getHealthp() != null) {
            data.put("healthp", entity.getHealthp());
        }
        if (entity.getHunger() != null) {
            data.put("hunger", entity.getHunger());
        }
        if (entity.getMagic() != null) {
            data.put("magic", entity.getMagic());
        }
        if (entity.getMagicp() != null) {
            data.put("magicp", entity.getMagicp());
        }

        Object items = deserializeMap(entity.getItems());
        if (items != null) {
            data.put("items", items);
        }

        Object matchItems = deserializeMap(entity.getMatchItems());
        if (matchItems != null) {
            data.put("matchItems", matchItems);
        }

        if (entity.getGamemode() != null) {
            data.put("gamemode", entity.getGamemode());
        }
        if (entity.getGodmode() != null) {
            data.put("godmode", entity.getGodmode());
        }
        if (entity.getLocation() != null) {
            data.put("location", entity.getLocation());
        }

        Object effects = deserializeList(entity.getEffects());
        if (effects != null) {
            data.put("effects", effects);
        }
        if (entity.getFlight() != null) {
            data.put("flight", entity.getFlight());
        }
        if (entity.getArenaClass() != null) {
            data.put("arenaClass", entity.getArenaClass());
        }
        if (entity.getOldTeam() != null) {
            data.put("oldTeam", entity.getOldTeam());
        }
        if (entity.getMoney() != null) {
            data.put("money", entity.getMoney());
        }

        playerSave.fromMap(data);
        return playerSave;
    }

    private static Executor createAsyncExecutor(BattleArena plugin) {
        return task -> {
            if (plugin.isEnabled() && !plugin.getServer().isStopping()) {
                try {
                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
                } catch (IllegalPluginAccessException e) {
                    task.run();
                }
            } else {
                task.run();
            }
        };
    }

    private static Integer asInteger(Object value) {
        return value instanceof Number number ? number.intValue() : null;
    }

    private static Double asDouble(Object value) {
        return value instanceof Number number ? number.doubleValue() : null;
    }

    private static Boolean asBoolean(Object value) {
        return value instanceof Boolean bool ? bool : null;
    }

    // Items and matchItems are Maps with "armor" and "contents" keys containing Lists
    private String serializeMap(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Map<?, ?> map) {
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
        if (str == null || str.isEmpty()) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
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
        if (obj == null) {
            return "";
        }
        if (obj instanceof List<?> list) {
            return list.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("|"));
        }
        return obj.toString();
    }

    private Object deserializeList(String str) {
        if (str == null || str.isEmpty()) {
            return new ArrayList<String>();
        }
        List<String> list = new ArrayList<>();
        for (String item : str.split("\\|")) {
            if (!item.trim().isEmpty()) {
                list.add(item.trim());
            }
        }
        return list;
    }
}
