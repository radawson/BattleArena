package org.clockworx.battlearena.storage;

import org.clockworx.battlearena.BattleArena;
import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.competition.PlayerStorage;
import org.clockworx.battlearena.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Adapter class that bridges PlayerStorage (in-memory) and StorageManager (persistence).
 * Handles conversion between PlayerStorage data and PlayerSave format using Paper APIs.
 */
public class StorageAdapter {
    private final BattleArena plugin;
    private final StorageManager storageManager;

    public StorageAdapter(BattleArena plugin, StorageManager storageManager) {
        this.plugin = plugin;
        this.storageManager = storageManager;
    }

    /**
     * Persists PlayerStorage data to the storage system asynchronously.
     *
     * @param storage The PlayerStorage to persist
     * @return CompletableFuture that completes when persistence is done
     */
    public CompletableFuture<Void> persist(PlayerStorage storage) {
        if (!storageManager.isAvailable()) {
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.runAsync(() -> {
            try {
                PlayerSave playerSave = toPlayerSave(storage);
                storageManager.savePlayerData(playerSave).join();
            } catch (Exception e) {
                plugin.error("Failed to persist player data for " + storage.getPlayer().getPlayer().getName(), e);
            }
        });
    }

    /**
     * Loads persisted data and applies it to PlayerStorage.
     * Must be called on the main thread for inventory restoration.
     *
     * @param uuid The player UUID
     * @param storage The PlayerStorage to apply data to
     * @return CompletableFuture that completes when loading is done
     */
    public CompletableFuture<Void> load(UUID uuid, PlayerStorage storage) {
        if (!storageManager.isAvailable()) {
            return CompletableFuture.completedFuture(null);
        }

        return storageManager.loadPlayerData(uuid)
            .thenAccept(opt -> {
                if (opt.isPresent()) {
                    // Apply on main thread for safety
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        fromPlayerSave(opt.get(), storage);
                    });
                }
            })
            .exceptionally(ex -> {
                plugin.error("Failed to load player data for " + uuid, ex);
                return null;
            });
    }

    /**
     * Converts PlayerStorage to PlayerSave format.
     *
     * @param storage The PlayerStorage to convert
     * @return The PlayerSave object
     */
    public PlayerSave toPlayerSave(PlayerStorage storage) {
        ArenaPlayer arenaPlayer = storage.getPlayer();
        PlayerSave save = new PlayerSave(
            arenaPlayer.getPlayer().getUniqueId(),
            arenaPlayer.getPlayer().getName()
        );

        // Inventory - serialize using Paper's ItemStack.serializeAsBytes()
        if (storage.getInventory() != null) {
            save.setInventory(storage.getInventory());
        }

        // GameMode
        if (storage.getGameMode() != null) {
            save.setGamemode(storage.getGameMode());
        }

        // Health and Hunger
        save.setHealth(storage.getHealth());
        save.setHunger(storage.getHunger());

        // Experience
        save.setTotalExp(storage.getTotalExp());
        save.setExp(storage.getExp());
        save.setExpLevels(storage.getExpLevels());

        // Attributes
        save.setAttributes(storage.getAttributes());

        // Speeds
        save.setWalkSpeed(storage.getWalkSpeed());
        save.setFlySpeed(storage.getFlySpeed());

        // Flight
        save.setFlight(storage.isFlight());
        save.setAllowFlight(storage.isAllowFlight());

        // Effects
        if (!storage.getEffects().isEmpty()) {
            save.setEffects(new ArrayList<>(storage.getEffects()));
        }

        // Location
        if (storage.getLastLocation() != null) {
            save.setLocation(storage.getLastLocation());
        }

        return save;
    }

    /**
     * Applies PlayerSave data to PlayerStorage.
     * Must be called on the main thread.
     *
     * @param save The PlayerSave to apply
     * @param storage The PlayerStorage to apply to
     */
    public void fromPlayerSave(PlayerSave save, PlayerStorage storage) {
        // Inventory - deserialize using Paper's ItemStack.deserializeBytes()
        if (save.getInventory() != null) {
            storage.setInventory(save.getInventory());
        }

        // GameMode
        if (save.getGamemode() != null) {
            storage.setGameMode(save.getGamemode());
        }

        // Health and Hunger
        if (save.getHealth() != null) {
            storage.setHealth(save.getHealth());
        }
        if (save.getHunger() != null) {
            storage.setHunger(save.getHunger());
        }

        // Experience
        if (save.getTotalExp() != null) {
            storage.setTotalExp(save.getTotalExp());
        }
        if (save.getExp() != null) {
            storage.setExp(save.getExp());
        }
        if (save.getExpLevels() != null) {
            storage.setExpLevels(save.getExpLevels());
        }

        // Attributes
        if (save.getAttributes() != null) {
            storage.setAttributes(save.getAttributes());
        }

        // Speeds
        if (save.getWalkSpeed() != null) {
            storage.setWalkSpeed(save.getWalkSpeed());
        }
        if (save.getFlySpeed() != null) {
            storage.setFlySpeed(save.getFlySpeed());
        }

        // Flight
        if (save.isFlight() != null) {
            storage.setFlight(save.isFlight());
        }
        if (save.isAllowFlight() != null) {
            storage.setAllowFlight(save.isAllowFlight());
        }

        // Effects
        if (save.getEffects() != null) {
            storage.setEffects(save.getEffects());
        }

        // Location
        if (save.getLocation() != null) {
            storage.setLastLocation(save.getLocation());
        }
    }

    /**
     * Helper method to serialize inventory using Paper API.
     *
     * @param inventory The inventory array
     * @return Base64 encoded string
     */
    public static String serializeInventory(ItemStack[] inventory) {
        if (inventory == null) {
            return null;
        }

        try {
            List<byte[]> itemBytes = new ArrayList<>();
            for (ItemStack item : inventory) {
                if (item == null) {
                    itemBytes.add(null);
                } else {
                    itemBytes.add(item.serializeAsBytes());
                }
            }

            // Serialize as: length, then each item's length (or -1 for null), then item bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeInt(itemBytes.size());
            for (byte[] bytes : itemBytes) {
                if (bytes == null) {
                    dos.writeInt(-1);
                } else {
                    dos.writeInt(bytes.length);
                    dos.write(bytes);
                }
            }
            dos.close();

            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize inventory", e);
        }
    }

    /**
     * Helper method to deserialize inventory using Paper API.
     *
     * @param serialized The Base64 encoded string
     * @return The inventory array
     */
    public static ItemStack[] deserializeInventory(String serialized) {
        if (serialized == null || serialized.isEmpty()) {
            return null;
        }

        try {
            byte[] data = Base64.getDecoder().decode(serialized);
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
            int length = dis.readInt();
            ItemStack[] inventory = new ItemStack[length];

            for (int i = 0; i < length; i++) {
                int itemLength = dis.readInt();
                if (itemLength == -1) {
                    inventory[i] = null;
                } else {
                    byte[] itemBytes = new byte[itemLength];
                    dis.readFully(itemBytes);
                    inventory[i] = ItemStack.deserializeBytes(itemBytes);
                }
            }
            dis.close();

            return inventory;
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize inventory", e);
        }
    }
}
