package org.clockworx.battlearena.event.player;

import org.clockworx.battlearena.Arena;
import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.event.EventTrigger;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called for {@link ArenaPlayer}s who exhaust all of their lives
 * in an {@link Arena}.
 */
@EventTrigger("on-lives-exhaust")
public class ArenaLivesExhaustEvent extends BukkitArenaPlayerEvent {
    private final static HandlerList HANDLERS = new HandlerList();

    public ArenaLivesExhaustEvent(@NotNull Arena arena, @NotNull ArenaPlayer player) {
        super(arena, player);
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
