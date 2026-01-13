package org.clockworx.battlearena.event.arena;

import org.clockworx.battlearena.Arena;
import org.clockworx.battlearena.competition.Competition;
import org.clockworx.battlearena.event.ArenaEvent;
import org.clockworx.battlearena.event.EventTrigger;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when an {@link Arena} ends in a draw.
 */
@EventTrigger("on-draw")
public class ArenaDrawEvent extends Event implements ArenaEvent {
    private final static HandlerList HANDLERS = new HandlerList();

    private final Arena arena;
    private final Competition<?> competition;

    public ArenaDrawEvent(Arena arena, Competition<?> competition) {
        this.arena = arena;
        this.competition = competition;
    }

    @Override
    public Arena getArena() {
        return this.arena;
    }

    @Override
    public Competition<?> getCompetition() {
        return this.competition;
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
