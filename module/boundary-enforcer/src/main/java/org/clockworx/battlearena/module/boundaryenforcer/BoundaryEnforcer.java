package org.clockworx.battlearena.module.boundaryenforcer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.competition.map.options.Bounds;
import org.clockworx.battlearena.event.ArenaEventHandler;
import org.clockworx.battlearena.event.ArenaListener;
import org.clockworx.battlearena.event.arena.ArenaInitializeEvent;
import org.clockworx.battlearena.messages.Message;
import org.clockworx.battlearena.messages.Messages;
import org.clockworx.battlearena.module.ArenaModule;
import org.clockworx.battlearena.module.ArenaModuleInitializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A module that enforces game boundaries and ensures players do not leave it.
 */
@ArenaModule(id = BoundaryEnforcer.ID, name = "Boundary Enforcer", description = "Enforces game boundaries and ensures players do not leave it.", authors = "BattlePlugins")
public class BoundaryEnforcer implements ArenaModuleInitializer, ArenaListener {
    public static final String ID = "boundary-enforcer";

    private static final long ALERT_INTERVAL = 2000L;
    private static final Message CANNOT_LEAVE_ARENA = Messages.message("cannot-leave-arena", Component.text("You cannot leave the arena!", NamedTextColor.RED));

    private final Map<UUID, Long> lastAlert = new HashMap<>();

    @EventHandler
    public void onArenaInitialize(ArenaInitializeEvent event) {
        if (!event.getArena().isModuleEnabled(ID)) {
            return;
        }

        event.getArena().getEventManager().registerEvents(this);
    }

    @ArenaEventHandler
    public void onMove(PlayerMoveEvent event, ArenaPlayer player) {
        // Check to see if the player has changed blocks
        if (event.getFrom().toBlock().equals(event.getTo().toBlock())) {
            return;
        }

        // Check to see if the player is in the arena
        Bounds bounds = player.getCompetition().getMap().getBounds();
        if (bounds == null || bounds.isInside(event.getTo().getBlockX(), event.getTo().getBlockY(), event.getTo().getBlockZ())) {
            return;
        }

        event.setCancelled(true);

        // Check to see if the player has been alerted recently
        if (!this.lastAlert.containsKey(player.getPlayer().getUniqueId()) || System.currentTimeMillis() - this.lastAlert.get(player.getPlayer().getUniqueId()) >= ALERT_INTERVAL) {
            CANNOT_LEAVE_ARENA.send(player.getPlayer());
            this.lastAlert.put(player.getPlayer().getUniqueId(), System.currentTimeMillis());
        }
    }
}
