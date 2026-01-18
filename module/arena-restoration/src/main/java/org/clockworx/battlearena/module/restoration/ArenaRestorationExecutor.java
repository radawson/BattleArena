package org.clockworx.battlearena.module.restoration;

import org.clockworx.battlearena.Arena;
import org.clockworx.battlearena.command.ArenaCommand;
import org.clockworx.battlearena.command.SubCommandExecutor;
import org.clockworx.battlearena.competition.Competition;
import org.clockworx.battlearena.competition.LiveCompetition;
import org.clockworx.battlearena.competition.map.options.Bounds;
import org.clockworx.battlearena.util.WorldEditAdapter;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Executor for arena restoration commands.
 * Uses WorldEditAdapter to avoid classloading issues.
 */
public class ArenaRestorationExecutor implements SubCommandExecutor {
    private final ArenaRestoration module;
    private final Arena arena;

    public ArenaRestorationExecutor(ArenaRestoration module, Arena arena) {
        this.module = module;
        this.arena = arena;
    }

    @ArenaCommand(commands = "schematic", description = "Creates a schematic for the specified arena from the map bounds.", permissionNode = "region")
    public void region(Player player, Competition<?> competition) {
        if (!(competition instanceof LiveCompetition<?> liveCompetition)) {
            return; // Cannot restore a non-live competition
        }

        Bounds bounds = liveCompetition.getMap().getBounds();
        if (bounds == null) {
            // No bounds
            ArenaRestoration.NO_BOUNDS.send(player);
            return;
        }

        // Use WorldEditAdapter instead of direct WorldEdit calls
        WorldEditAdapter adapter = WorldEditAdapter.create(this.arena.getPlugin().getServer().getPluginManager());
        if (adapter == null || !adapter.isAvailable()) {
            ArenaRestoration.FAILED_TO_CREATE_SCHEMATIC.send(player);
            this.arena.getPlugin().error("WorldEdit/FAWE is not available for creating schematics");
            return;
        }

        // Create schematic from clipboard
        Object clipboard = adapter.createSchematic(liveCompetition.getMap().getWorld(), bounds);
        if (clipboard == null) {
            ArenaRestoration.FAILED_TO_CREATE_SCHEMATIC.send(player);
            this.arena.getPlugin().error("Failed to create schematic for map {} in arena {}", competition.getMap().getName(), this.arena.getName());
            return;
        }

        Path path = this.module.getSchematicPath(this.arena, competition);
        if (Files.notExists(path.getParent())) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                ArenaRestoration.FAILED_TO_CREATE_SCHEMATIC.send(player);
                this.arena.getPlugin().error("Failed to create schematic directory for map {} in arena {}", competition.getMap().getName(), this.arena.getName(), e);
                return;
            }
        }

        if (adapter.writeSchematic(clipboard, path)) {
            ArenaRestoration.SCHEMATIC_CREATED.send(player, competition.getMap().getName());
        } else {
            ArenaRestoration.FAILED_TO_CREATE_SCHEMATIC.send(player);
            this.arena.getPlugin().error("Failed to write schematic for map {} in arena {}", competition.getMap().getName(), this.arena.getName());
        }
    }
}
