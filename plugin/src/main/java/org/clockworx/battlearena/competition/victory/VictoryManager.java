package org.clockworx.battlearena.competition.victory;

import org.clockworx.battlearena.Arena;
import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.competition.Competition;
import org.clockworx.battlearena.competition.CompetitionLike;
import org.clockworx.battlearena.competition.LiveCompetition;
import org.clockworx.battlearena.competition.phase.CompetitionPhaseType;
import org.clockworx.battlearena.event.ArenaEventHandler;
import org.clockworx.battlearena.event.ArenaListener;
import org.clockworx.battlearena.event.arena.ArenaPhaseStartEvent;
import org.clockworx.battlearena.resolver.Resolvable;
import org.clockworx.battlearena.resolver.Resolver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Manages the victory conditions of a competition.
 *
 * @param <T> the type of competition
 */
public class VictoryManager<T extends Competition<T>> implements ArenaListener, CompetitionLike<T>, Resolvable {
    private final Map<VictoryConditionType<?, ?>, VictoryCondition<?>> victoryConditions = new HashMap<>();

    private final Arena arena;
    private final T competition;

    private boolean closed = false;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public VictoryManager(Arena arena, T competition) {
        this.arena = arena;
        this.competition = competition;

        for (Map.Entry<VictoryConditionType<?, ?>, VictoryConditionType.Provider<?, ?>> entry : arena.getVictoryConditions().entrySet()) {
            VictoryConditionType<?, ?> type = entry.getKey();
            VictoryCondition<?> condition = ((VictoryConditionType.Provider) entry.getValue()).create((LiveCompetition) competition);

            arena.getEventManager().registerEvents(condition);
            this.victoryConditions.put(type, condition);

            arena.getPlugin().debug("Registered victory condition: {} in competition: {}", condition.getClass().getSimpleName(), competition.getClass().getSimpleName());
        }

        arena.getEventManager().registerEvents(this);
    }

    /**
     * Identifies the potential victors of the competition.
     *
     * @return the potential victors of the competition
     */
    public Set<ArenaPlayer> identifyPotentialVictors() {
        Set<ArenaPlayer> victors = new HashSet<>();
        int conditionsWithVictors = 0;
        for (VictoryCondition<?> condition : this.victoryConditions.values()) {
            Set<ArenaPlayer> potentialVictors = new HashSet<>(condition.identifyPotentialVictors());

            // Remove existing victors to avoid double counting
            potentialVictors.removeAll(victors);

            victors.addAll(potentialVictors);
            if (!potentialVictors.isEmpty()) {
                conditionsWithVictors++;
            }
        }

        // If we have victory conditions with multiple victors,
        // we cannot realistically determine who actually "won"
        // the game. Future idea: Make this configurable? Need to
        // find a compelling use case for this.
        if (conditionsWithVictors > 1) {
            return Set.of();
        }

        return victors;
    }

    /**
     * Ends the victory manager.
     *
     * @param closed whether the victory manager is closed
     */
    public void end(boolean closed) {
        this.closed = closed;

        for (VictoryCondition<?> condition : this.victoryConditions.values()) {
            condition.end();
        }

        if (closed) {
            this.arena.getEventManager().unregisterEvents(this);
            for (VictoryCondition<?> condition : this.victoryConditions.values()) {
                this.arena.getEventManager().unregisterEvents(condition);
            }
        }
    }

    @ArenaEventHandler
    public void onPhaseStart(ArenaPhaseStartEvent event) {
        if (CompetitionPhaseType.VICTORY.equals(event.getPhase().getNextPhase())) {
            // Start the victory conditions
            for (VictoryCondition<?> condition : this.victoryConditions.values()) {
                condition.start();
            }
        }
    }

    /**
     * Returns the {@link LiveCompetition} this team manager is managing.
     *
     * @return the competition this team manager is managing
     */
    @Override
    public T getCompetition() {
        return this.competition;
    }

    /**
     * Returns whether the victory manager is closed.
     *
     * @return whether the victory manager is closed
     */
    public final boolean isClosed() {
        return this.closed;
    }

    @Override
    public Resolver resolve() {
        Resolver.Builder builder = Resolver.builder();
        for (VictoryCondition<?> value : this.victoryConditions.values()) {
            value.resolve().mergeInto(builder);
        }

        return builder.build();
    }
}
