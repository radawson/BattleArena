package mc.alk.arena.storage;

import mc.alk.arena.objects.PlayerSave;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Interface defining storage operations for player data.
 * 
 * This abstraction layer allows the plugin to support multiple storage backends:
 * - FlatFile: Simple YAML file-based storage (human-readable, good for small servers)
 * - SQLite: Embedded database (recommended default, no server required)
 * - MySQL: Network database (for multi-server deployments)
 * 
 * All operations that may be slow (I/O, network) return CompletableFuture
 * to allow async execution and prevent blocking the main server thread.
 * 
 * Implementations must be thread-safe as operations may be called from
 * multiple threads (main thread, async scheduler, etc.).
 */
public interface StorageProvider {
    
    /**
     * Gets the name of this storage provider.
     * Used for logging and configuration.
     * 
     * @return The provider name (e.g., "flatfile", "sqlite", "mysql")
     */
    String getName();
    
    /**
     * Initializes the storage provider.
     * This is called once when the plugin enables.
     * 
     * For database providers, this should:
     * - Establish connection(s)
     * - Create tables if they don't exist
     * - Run any necessary migrations
     * 
     * @return CompletableFuture that completes when initialization is done
     * @throws StorageException if initialization fails
     */
    CompletableFuture<Void> initialize();
    
    /**
     * Shuts down the storage provider.
     * This is called when the plugin disables.
     * 
     * For database providers, this should:
     * - Flush any pending writes
     * - Close connection pools
     * - Clean up resources
     * 
     * @return CompletableFuture that completes when shutdown is done
     */
    CompletableFuture<Void> shutdown();
    
    /**
     * Checks if the storage provider is currently connected/available.
     * 
     * @return true if the provider is ready to accept operations
     */
    boolean isAvailable();
    
    // ==================== Player Data CRUD Operations ====================
    
    /**
     * Saves player data to storage.
     * If the player data already exists, it will be updated.
     * 
     * @param uuid The player's UUID
     * @param playerSave The PlayerSave object containing all player data
     * @return CompletableFuture that completes when the save is done
     * @throws StorageException if the save fails
     */
    CompletableFuture<Void> savePlayerData(UUID uuid, PlayerSave playerSave);
    
    /**
     * Loads player data by UUID.
     * 
     * @param uuid The player's UUID
     * @return CompletableFuture containing the PlayerSave if found, null otherwise
     */
    CompletableFuture<PlayerSave> loadPlayerData(UUID uuid);
    
    /**
     * Deletes player data from storage.
     * 
     * @param uuid The UUID of the player to delete
     * @return CompletableFuture that completes with true if deleted, false if not found
     */
    CompletableFuture<Boolean> deletePlayerData(UUID uuid);
    
    /**
     * Checks if player data exists in storage.
     * 
     * @param uuid The player UUID
     * @return CompletableFuture that completes with true if exists
     */
    CompletableFuture<Boolean> playerDataExists(UUID uuid);
    
    /**
     * Loads all player data from storage.
     * Use with caution on large datasets.
     * 
     * @return CompletableFuture containing list of all PlayerSave objects
     */
    CompletableFuture<List<PlayerSave>> loadAllPlayerData();
}
