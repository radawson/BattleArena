package org.clockworx.battlearena.event.player;

import org.clockworx.battlearena.Arena;
import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.competition.LiveCompetition;
import org.clockworx.battlearena.event.ArenaEvent;
import org.clockworx.battlearena.resolver.Resolver;

/**
 * Represents an event that occurs in an {@link Arena} to a
 * {@link ArenaPlayer}.
 */
public interface ArenaPlayerEvent extends ArenaEvent {

    @Override
    default Arena getArena() {
        return this.getArenaPlayer().getArena();
    }

    @Override
    default LiveCompetition<?> getCompetition() {
        return this.getArenaPlayer().getCompetition();
    }

    /**
     * Gets the {@link ArenaPlayer} this event is occurring to.
     *
     * @return the arena player this event is occurring to
     */
    ArenaPlayer getArenaPlayer();

    /**
     * Resolves the {@link ArenaPlayer} this event is occurring to
     * to a {@link Resolver} object.
     *
     * @return the resolved object
     */
    default Resolver resolve() {
        return this.getArenaPlayer().resolve();
    }
}
