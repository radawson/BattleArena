package org.clockworx.battlearena.module.restoration;

import org.clockworx.battlearena.Arena;
import org.clockworx.battlearena.competition.LiveCompetition;
import org.clockworx.battlearena.competition.map.options.Bounds;
import org.clockworx.battlearena.util.WorldEditAdapter;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class for arena restoration operations.
 * Uses WorldEditAdapter to avoid classloading issues.
 */
class ArenaRestorationUtil {

    public static void restoreArena(ArenaRestoration module, Arena arena, LiveCompetition<?> competition, Bounds bounds) {
        Path path = module.getSchematicPath(arena, competition);
        if (Files.notExists(path)) {
            // No schematic found
            arena.getPlugin().warn("Could not restore map {} for arena {} as no schematic was found!", competition.getMap().getName(), arena.getName());
            return;
        }

        // Use WorldEditAdapter instead of direct WorldEdit calls
        WorldEditAdapter adapter = WorldEditAdapter.create(arena.getPlugin().getServer().getPluginManager());
        if (adapter == null || !adapter.isAvailable()) {
            arena.getPlugin().error("WorldEdit/FAWE is not available for restoring arenas");
            return;
        }

        // Read schematic
        Object clipboard = adapter.readSchematic(path);
        if (clipboard == null) {
            arena.getPlugin().warn("Could not restore map {} for arena {} as the schematic could not be read!", competition.getMap().getName(), arena.getName());
            return;
        }

        // Paste schematic
        if (!adapter.pasteSchematic(clipboard, competition.getMap().getWorld(), bounds)) {
            arena.getPlugin().error("Failed to restore map {} for arena {} due to an error pasting the schematic!", competition.getMap().getName(), arena.getName());
        }
    }
}
