package org.clockworx.battlearena.util;

import org.clockworx.battlearena.BattleArena;
import org.clockworx.battlearena.competition.map.options.Bounds;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;

/**
 * Utility class for block operations using WorldEdit/FAWE.
 * Uses reflection-based adapter to avoid classloading issues when WorldEdit is not installed.
 */
public final class BlockUtil {

    /**
     * Copies a region from one world to another using WorldEdit/FAWE.
     * 
     * @param oldWorld The source world
     * @param newWorld The destination world
     * @param bounds The bounds to copy
     * @return true if successful
     */
    public static boolean copyToWorld(World oldWorld, World newWorld, Bounds bounds) {
        PluginManager pluginManager = BattleArena.getInstance().getServer().getPluginManager();
        WorldEditAdapter adapter = WorldEditAdapter.create(pluginManager);
        
        if (adapter == null || !adapter.isAvailable()) {
            BattleArena.getInstance().error("WorldEdit/FAWE is required to copy regions between worlds!");
            return false;
        }
        
        return adapter.copyToWorld(oldWorld, newWorld, bounds);
    }
}
