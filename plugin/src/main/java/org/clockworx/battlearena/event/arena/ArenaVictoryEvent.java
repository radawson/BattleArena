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
 * Called when players win a competition.
 * <p>
 * This event is triggered when victory conditions are met and winners are determined.
 * The {@link #getVictors()} method returns the set of winning players (or teams).
 * <p>
 * Common actions configured for this event include:
 * <ul>
 *   <li>{@code send-message{message=...}} - Congratulate winners</li>
 *   <li>{@code play-sound{...}} - Play victory sound</li>
 *   <li>{@code run-command{command=...}} - Execute rewards</li>
 * </ul>
 * <p>
 * Available resolver placeholders:
 * <ul>
 *   <li>{@code {player}} - Current player's name (when used in player-specific actions)</li>
 *   <li>{@code {players}} - All victors</li>
 *   <li>{@code {arena}} - Arena name</li>
 *   <li>{@code {competition}} - Competition/map name</li>
 * </ul>
 *
 * @see ArenaLoseEvent
 * @see ArenaDrawEvent
 */
@EventTrigger("on-victory")
public class ArenaVictoryEvent extends Event implements ArenaEvent {
    private final static HandlerList HANDLERS = new HandlerList();

    private final Arena arena;
    private final Competition<?> competition;
    private final Set<ArenaPlayer> victors;

    public ArenaVictoryEvent(Arena arena, Competition<?> competition, Set<ArenaPlayer> victors) {
        this.arena = arena;
        this.competition = competition;
        this.victors = victors;
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
     * Gets the victors of the competition.
     *
     * @return the victors of the competition
     */
    public Set<ArenaPlayer> getVictors() {
        return this.victors;
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
