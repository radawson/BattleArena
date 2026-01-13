package mc.alk.arena.storage;

import mc.alk.arena.BattleArena;
import mc.alk.arena.objects.PlayerSave;
import mc.alk.arena.util.Log;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Manages storage provider lifecycle and operations.
 * 
 * The StorageManager is responsible for:
 * - Selecting and initializing the appropriate storage provider based on config
 * - Providing a unified interface for storage operations
 * - Handling provider switching and data migration
 * - Managing async operation scheduling
 * 
 * Usage:
 * <pre>
 * StorageManager storage = new StorageManager(plugin);
 * storage.initialize().join(); // Wait for init
 * 
 * // Save player data
 * storage.savePlayerData(uuid, playerSave);
 * 
 * // Load player data
 * PlayerSave save = storage.loadPlayerData(uuid).join().orElse(null);
 * </pre>
 */
public class StorageManager {
    
    private final BattleArena plugin;
    private StorageProvider activeProvider;
    private StorageType activeType;
    
    /**
     * Enumeration of available storage types.
     */
    public enum StorageType {
        /** YAML file-based storage */
        FLATFILE("flatfile"),
        /** SQLite embedded database */
        SQLITE("sqlite"),
        /** MySQL/MariaDB network database */
        MYSQL("mysql");
        
        private final String id;
        
        StorageType(String id) {
            this.id = id;
        }
        
        public String getId() {
            return id;
        }
        
        public static StorageType fromId(String id) {
            if (id == null) return FLATFILE; // Default
            for (StorageType type : values()) {
                if (type.id.equalsIgnoreCase(id)) {
                    return type;
                }
            }
            return FLATFILE; // Default fallback
        }
    }
    
    /**
     * Creates a new StorageManager.
     * 
     * @param plugin The plugin instance
     */
    public StorageManager(BattleArena plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Initializes the storage manager with the configured provider.
     * Reads the storage type from config and initializes the appropriate provider.
     * 
     * @return CompletableFuture that completes when initialization is done
     */
    public CompletableFuture<Void> initialize() {
        // Read storage type from config (default to flatfile)
        String typeId = plugin.getConfig().getString("storage.type", "flatfile");
        this.activeType = StorageType.fromId(typeId);
        
        Log.info("[BattleArena] Initializing storage provider: " + activeType.name());
        
        // Create the appropriate provider
        this.activeProvider = createProvider(activeType);
        
        // Initialize the provider
        return activeProvider.initialize()
            .thenRun(() -> {
                Log.info("[BattleArena] Storage provider initialized: " + activeProvider.getName());
            })
            .exceptionally(ex -> {
                Log.err("[BattleArena] Failed to initialize storage provider: " + ex.getMessage());
                Log.printStackTrace(ex);
                throw new StorageException("Storage initialization failed", ex);
            });
    }
    
    /**
     * Shuts down the storage manager and active provider.
     * 
     * @return CompletableFuture that completes when shutdown is done
     */
    public CompletableFuture<Void> shutdown() {
        if (activeProvider != null) {
            Log.info("[BattleArena] Shutting down storage provider: " + activeProvider.getName());
            return activeProvider.shutdown()
                .thenRun(() -> {
                    Log.info("[BattleArena] Storage provider shut down successfully");
                })
                .exceptionally(ex -> {
                    Log.err("[BattleArena] Error during storage shutdown: " + ex.getMessage());
                    return null;
                });
        }
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Creates a storage provider of the specified type.
     * 
     * @param type The storage type
     * @return The created provider
     */
    private StorageProvider createProvider(StorageType type) {
        switch (type) {
            case FLATFILE:
                return new FlatFileStorageProvider(plugin);
            case SQLITE:
                return new SQLiteStorageProvider(plugin);
            case MYSQL:
                return new MySQLStorageProvider(plugin);
            default:
                throw new IllegalArgumentException("Unknown storage type: " + type);
        }
    }
    
    /**
     * Gets the active storage provider.
     * 
     * @return The active provider
     * @throws IllegalStateException if no provider is initialized
     */
    public StorageProvider getProvider() {
        if (activeProvider == null) {
            throw new IllegalStateException("Storage provider not initialized");
        }
        return activeProvider;
    }
    
    /**
     * Gets the active storage type.
     * 
     * @return The active type
     */
    public StorageType getActiveType() {
        return activeType;
    }
    
    /**
     * Checks if the storage is available and ready.
     * 
     * @return true if ready for operations
     */
    public boolean isAvailable() {
        return activeProvider != null && activeProvider.isAvailable();
    }
    
    // ==================== Delegated Operations ====================
    // These methods delegate to the active provider for convenience
    
    /**
     * Saves player data to storage.
     * 
     * @param uuid The player's UUID
     * @param playerSave The PlayerSave object
     * @return CompletableFuture that completes when saved
     */
    public CompletableFuture<Void> savePlayerData(UUID uuid, PlayerSave playerSave) {
        return getProvider().savePlayerData(uuid, playerSave);
    }
    
    /**
     * Loads player data by UUID.
     * 
     * @param uuid The player UUID
     * @return CompletableFuture with the PlayerSave if found, null otherwise
     */
    public CompletableFuture<PlayerSave> loadPlayerData(UUID uuid) {
        return getProvider().loadPlayerData(uuid);
    }
    
    /**
     * Deletes player data.
     * 
     * @param uuid The player UUID
     * @return CompletableFuture with true if deleted
     */
    public CompletableFuture<Boolean> deletePlayerData(UUID uuid) {
        return getProvider().deletePlayerData(uuid);
    }
    
    /**
     * Checks if player data exists.
     * 
     * @param uuid The player UUID
     * @return CompletableFuture with true if exists
     */
    public CompletableFuture<Boolean> playerDataExists(UUID uuid) {
        return getProvider().playerDataExists(uuid);
    }
    
    /**
     * Loads all player data.
     * 
     * @return CompletableFuture with list of all PlayerSave objects
     */
    public CompletableFuture<List<PlayerSave>> loadAllPlayerData() {
        return getProvider().loadAllPlayerData();
    }
    
    // ==================== Migration Operations ====================
    
    /**
     * Migrates data from one storage provider to another.
     * 
     * @param fromType Source storage type
     * @param toType Target storage type
     * @return CompletableFuture with the count of migrated players
     */
    public CompletableFuture<Integer> migrateData(StorageType fromType, StorageType toType) {
        if (fromType == toType) {
            return CompletableFuture.completedFuture(0);
        }
        
        Log.info("[BattleArena] Starting migration from " + fromType + " to " + toType);
        
        StorageProvider source = createProvider(fromType);
        StorageProvider target = createProvider(toType);
        
        return source.initialize()
            .thenCompose(v -> target.initialize())
            .thenCompose(v -> source.loadAllPlayerData())
            .thenCompose(playerSaves -> {
                Log.info("[BattleArena] Exporting " + playerSaves.size() + " player saves for migration");
                CompletableFuture<Void>[] saves = new CompletableFuture[playerSaves.size()];
                int i = 0;
                for (PlayerSave save : playerSaves) {
                    saves[i++] = target.savePlayerData(save.getID(), save);
                }
                return CompletableFuture.allOf(saves);
            })
            .thenCompose(v -> {
                int count = 0;
                try {
                    List<PlayerSave> saves = source.loadAllPlayerData().join();
                    count = saves.size();
                } catch (Exception e) {
                    Log.err("[BattleArena] Error counting migrated players: " + e.getMessage());
                }
                return source.shutdown()
                    .thenCompose(v2 -> target.shutdown())
                    .thenApply(v2 -> count);
            })
            .exceptionally(ex -> {
                Log.err("[BattleArena] Migration failed: " + ex.getMessage());
                Log.printStackTrace(ex);
                throw new StorageException("Migration failed", ex);
            });
    }
}
