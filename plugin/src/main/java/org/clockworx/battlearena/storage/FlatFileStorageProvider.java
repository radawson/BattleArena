package org.clockworx.battlearena.storage;

import org.clockworx.battlearena.BattleArena;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * YAML file-based storage provider for player data.
 * 
 * This provider uses YAML files in the saves/players/ directory.
 * Each player's data is stored in a separate file named {uuid}.yml.
 * 
 * This is the default storage type and maintains compatibility with
 * the existing inventory serialization system.
 */
public class FlatFileStorageProvider implements StorageProvider {
    
    private final BattleArena plugin;
    private File playersDir;
    private boolean available;
    
    public FlatFileStorageProvider(BattleArena plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "flatfile";
    }
    
    @Override
    public CompletableFuture<Void> initialize() {
        return CompletableFuture.runAsync(() -> {
            try {
                playersDir = new File(plugin.getDataFolder(), "saves/players");
                if (!playersDir.exists()) {
                    playersDir.mkdirs();
                }
                available = true;
            } catch (Exception e) {
                plugin.error("Failed to initialize flatfile storage: " + e.getMessage(), e);
                available = false;
                throw new StorageException("Failed to initialize flatfile storage", e);
            }
        });
    }
    
    @Override
    public CompletableFuture<Void> shutdown() {
        return CompletableFuture.completedFuture(null);
    }
    
    @Override
    public boolean isAvailable() {
        return available;
    }
    
    @Override
    public CompletableFuture<Void> savePlayerData(PlayerSave playerSave) {
        return CompletableFuture.runAsync(() -> {
            try {
                File file = new File(playersDir, playerSave.getID().toString() + ".yml");
                YamlConfiguration config = new YamlConfiguration();
                
                // Serialize PlayerSave to map
                Map<String, Object> data = playerSave.toMap();
                
                // Write to configuration
                ConfigurationSection cs = config.createSection("data");
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    cs.set(entry.getKey(), entry.getValue());
                }
                
                // Save player name for reference
                config.set("playerName", playerSave.getName());
                config.set("uuid", playerSave.getID().toString());
                
                config.save(file);
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
                File file = new File(playersDir, uuid.toString() + ".yml");
                if (!file.exists()) {
                    return Optional.empty();
                }
                
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection cs = config.getConfigurationSection("data");
                if (cs == null) {
                    return Optional.empty();
                }
                
                // TODO: Properly integrate with new codebase's ArenaPlayer
                // For now, create a minimal PlayerSave
                // This needs to be adapted to work with the new codebase structure
                String playerName = config.getString("playerName", "Unknown");
                PlayerSave playerSave = new PlayerSave(uuid, playerName);
                
                // Convert ConfigurationSection to Map
                Map<String, Object> data = cs.getValues(false);
                playerSave.fromMap(data);
                
                return Optional.of(playerSave);
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
                File file = new File(playersDir, uuid.toString() + ".yml");
                if (file.exists()) {
                    return file.delete();
                }
                return false;
            } catch (Exception e) {
                plugin.error("Failed to delete player data for " + uuid + ": " + e.getMessage(), e);
                return false;
            }
        });
    }
    
    @Override
    public CompletableFuture<Boolean> playerDataExists(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            File file = new File(playersDir, uuid.toString() + ".yml");
            return file.exists();
        });
    }
    
    @Override
    public CompletableFuture<List<PlayerSave>> loadAllPlayerData() {
        return CompletableFuture.supplyAsync(() -> {
            List<PlayerSave> playerSaves = new ArrayList<>();
            try {
                if (!playersDir.exists()) {
                    return playerSaves;
                }
                
                File[] files = playersDir.listFiles((dir, name) -> name.endsWith(".yml"));
                if (files == null) {
                    return playerSaves;
                }
                
                for (File file : files) {
                    try {
                        String fileName = file.getName();
                        String uuidStr = fileName.substring(0, fileName.length() - 4); // Remove .yml
                        UUID uuid = UUID.fromString(uuidStr);
                        
                        Optional<PlayerSave> saveOpt = loadPlayerData(uuid).join();
                        if (saveOpt.isPresent()) {
                            playerSaves.add(saveOpt.get());
                        }
                    } catch (Exception e) {
                        plugin.error("Failed to load player data from " + file.getName() + ": " + e.getMessage(), e);
                    }
                }
            } catch (Exception e) {
                plugin.error("Failed to load all player data: " + e.getMessage(), e);
            }
            return playerSaves;
        });
    }
}
