package org.clockworx.battlearena.event.arena;

import org.clockworx.battlearena.Arena;
import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.competition.Competition;
import org.clockworx.battlearena.event.ArenaEvent;
import org.clockworx.battlearena.event.EventTrigger;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Called for {@link ArenaPlayer}s who lose in an {@link Arena}.
 */
@EventTrigger("on-lose")
public class ArenaLoseEvent extends Event implements ArenaEvent {
    private final static HandlerList HANDLERS = new HandlerList();

    private final Arena arena;
    private final Competition<?> competition;
    private final Set<ArenaPlayer> losers;

    public ArenaLoseEvent(Arena arena, Competition<?> competition, Set<ArenaPlayer> losers) {
        this.arena = arena;
        this.competition = competition;
        this.losers = losers;
    }

    @Override
    public Arena getArena() {
        return this.arena;
    }

    @Override
    public Competition<?> getCompetition() {
        return this.competition;
    }

    /**
     * Gets the losers of the competition.
     *
     * @return the losers of the competition
     */
    public Set<ArenaPlayer> getLosers() {
        return this.losers;
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
