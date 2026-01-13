package org.clockworx.battlearena.event.player;

import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.event.EventTrigger;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player spectates an arena.
 */
@EventTrigger("on-spectate")
public class ArenaSpectateEvent extends BukkitArenaPlayerEvent {
    private final static HandlerList HANDLERS = new HandlerList();

    public ArenaSpectateEvent(ArenaPlayer player) {
        super(player.getArena(), player);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
