package mc.alk.arena.storage;

import mc.alk.arena.BattleArena;
import mc.alk.arena.objects.ArenaPlayer;
import mc.alk.arena.objects.PlayerSave;
import mc.alk.arena.serializers.BaseConfig;
import mc.alk.arena.util.Log;
import mc.alk.arena.util.PlayerController;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
                Log.err("[BattleArena] Failed to initialize flatfile storage: " + e.getMessage());
                Log.printStackTrace(e);
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
    public CompletableFuture<Void> savePlayerData(UUID uuid, PlayerSave playerSave) {
        return CompletableFuture.runAsync(() -> {
            try {
                BaseConfig config = new BaseConfig();
                File file = new File(playersDir, uuid.toString() + ".yml");
                if (!config.setConfig(file)) {
                    throw new StorageException("Failed to create config file for " + uuid);
                }
                
                // Serialize PlayerSave to map
                Map<String, Object> data = playerSave.toMap();
                
                // Write to configuration
                ConfigurationSection cs = config.getConfig().createSection("data");
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    cs.set(entry.getKey(), entry.getValue());
                }
                
                // Save player name for reference
                config.getConfig().set("playerName", playerSave.getName());
                config.getConfig().set("uuid", uuid.toString());
                
                config.save();
            } catch (Exception e) {
                Log.err("[BattleArena] Failed to save player data for " + uuid + ": " + e.getMessage());
                Log.printStackTrace(e);
                throw new StorageException("Failed to save player data", e);
            }
        });
    }
    
    @Override
    public CompletableFuture<PlayerSave> loadPlayerData(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                BaseConfig config = new BaseConfig();
                File file = new File(playersDir, uuid.toString() + ".yml");
                if (!file.exists()) {
                    return null;
                }
                
                if (!config.setConfig(file)) {
                    return null;
                }
                
                ConfigurationSection cs = config.getConfig().getConfigurationSection("data");
                if (cs == null) {
                    return null;
                }
                
                // Get or create ArenaPlayer
                ArenaPlayer arenaPlayer = PlayerController.toArenaPlayer(uuid);
                if (arenaPlayer == null) {
                    return null;
                }
                
                // Create PlayerSave and deserialize
                PlayerSave playerSave = new PlayerSave(arenaPlayer);
                
                // Convert ConfigurationSection to Map
                Map<String, Object> data = cs.getValues(false);
                playerSave.fromMap(data);
                
                return playerSave;
            } catch (Exception e) {
                Log.err("[BattleArena] Failed to load player data for " + uuid + ": " + e.getMessage());
                Log.printStackTrace(e);
                return null;
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
                Log.err("[BattleArena] Failed to delete player data for " + uuid + ": " + e.getMessage());
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
            List<PlayerSave> playerSaves = new ArrayList<PlayerSave>();
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
                        
                        PlayerSave save = loadPlayerData(uuid).join();
                        if (save != null) {
                            playerSaves.add(save);
                        }
                    } catch (Exception e) {
                        Log.err("[BattleArena] Failed to load player data from " + file.getName() + ": " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                Log.err("[BattleArena] Failed to load all player data: " + e.getMessage());
                Log.printStackTrace(e);
            }
            return playerSaves;
        });
    }
}
