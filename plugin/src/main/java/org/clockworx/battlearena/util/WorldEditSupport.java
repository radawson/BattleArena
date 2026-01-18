package org.clockworx.battlearena.util;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.Nullable;

/**
 * Utility for detecting WorldEdit availability in a way that works for both
 * WorldEdit and FastAsyncWorldEdit (FAWE). We rely on reflection to avoid
 * hard-linking to a specific plugin class loader and to keep the check safe
 * even when neither plugin is installed.
 */
public final class WorldEditSupport {
    private static final String WORLD_EDIT_CLASS = "com.sk89q.worldedit.WorldEdit";
    private static final String WORLD_EDIT_PLUGIN = "WorldEdit";
    private static final String FAWE_PLUGIN = "FastAsyncWorldEdit";

    private WorldEditSupport() {
    }

    /**
     * Returns true if WorldEdit APIs are available via either WorldEdit or FAWE.
     *
     * @param pluginManager The server plugin manager.
     * @return True when a compatible plugin is enabled and exposes WorldEdit classes.
     */
    public static boolean isWorldEditAvailable(PluginManager pluginManager) {
        Plugin plugin = resolveWorldEditPlugin(pluginManager);
        if (plugin == null) {
            return false;
        }

        return isClassAvailable(plugin, WORLD_EDIT_CLASS);
    }

    /**
     * Returns the enabled plugin that provides the WorldEdit API.
     * Prefer WorldEdit if both are present for consistency.
     *
     * @param pluginManager The server plugin manager.
     * @return The enabled plugin instance, or null when neither is present.
     */
    @Nullable
    public static Plugin resolveWorldEditPlugin(PluginManager pluginManager) {
        Plugin worldEdit = getEnabledPlugin(pluginManager, WORLD_EDIT_PLUGIN);
        if (worldEdit != null) {
            return worldEdit;
        }

        return getEnabledPlugin(pluginManager, FAWE_PLUGIN);
    }

    private static boolean isClassAvailable(Plugin plugin, String className) {
        try {
            plugin.getClass().getClassLoader().loadClass(className);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    @Nullable
    private static Plugin getEnabledPlugin(PluginManager pluginManager, String name) {
        Plugin plugin = pluginManager.getPlugin(name);
        if (plugin == null || !plugin.isEnabled()) {
            return null;
        }

        return plugin;
    }
}
