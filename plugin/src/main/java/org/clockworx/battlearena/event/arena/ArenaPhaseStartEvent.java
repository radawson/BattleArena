package org.clockworx.battlearena.event.arena;

import org.clockworx.battlearena.Arena;
import org.clockworx.battlearena.competition.Competition;
import org.clockworx.battlearena.competition.phase.CompetitionPhase;
import org.clockworx.battlearena.event.ArenaEvent;
import org.clockworx.battlearena.event.EventTrigger;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a competition phase starts.
 * <p>
 * This event is triggered when a competition transitions to a new phase
 * (e.g., from waiting to countdown, or from countdown to ingame).
 * <p>
 * This event is typically configured at the phase level in the arena configuration:
 * <pre>{@code
 * phases:
 *   ingame:
 *     events:
 *       on-start:
 *         - broadcast{message=Game starting!;audience=game}
 *         - teleport{location=team_spawn}
 * }</pre>
 * <p>
 * Available resolver placeholders:
 * <ul>
 *   <li>{@code {arena}} - Arena name</li>
 *   <li>{@code {competition}} - Competition/map name</li>
 *   <li>{@code {phase}} - Current phase name</li>
 * </ul>
 *
 * @see ArenaPhaseCompleteEvent
 * @see org.clockworx.battlearena.competition.phase.CompetitionPhase
 */
@EventTrigger("on-start")
public class ArenaPhaseStartEvent extends Event implements ArenaEvent {
    private final static HandlerList HANDLERS = new HandlerList();

    private final Arena arena;
    private final Competition<?> competition;
    private final CompetitionPhase<?> phase;

    public ArenaPhaseStartEvent(Arena arena, Competition<?> competition, CompetitionPhase<?> phase) {
        this.arena = arena;
        this.competition = competition;
        this.phase = phase;
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
     * Gets the phase that was completed.
     *
     * @return the phase that was completed
     */
    public CompetitionPhase<?> getPhase() {
        return this.phase;
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
