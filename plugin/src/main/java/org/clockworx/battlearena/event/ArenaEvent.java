package org.clockworx.battlearena.event;

import org.clockworx.battlearena.Arena;
import org.clockworx.battlearena.competition.Competition;
import org.clockworx.battlearena.resolver.Resolvable;
import org.clockworx.battlearena.resolver.Resolver;
import org.clockworx.battlearena.resolver.ResolverKeys;
import org.clockworx.battlearena.resolver.ResolverProvider;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an event that occurs in an {@link Arena}.
 * <p>
 * Arena events are the foundation of BattleArena's event-driven architecture.
 * When an event is triggered, it can be configured to execute a series of
 * {@link org.clockworx.battlearena.event.action.EventAction actions} that
 * modify game behavior.
 * <p>
 * Events can be defined at two levels:
 * <ul>
 *   <li><b>Arena-level</b>: Defined in the root {@code events:} section of
 *   arena configuration, applies to all competitions for that arena.</li>
 *   <li><b>Phase-level</b>: Defined in {@code phases:<phase-name>:events:},
 *   applies only during that specific phase and overrides arena-level events.</li>
 * </ul>
 * <p>
 * Events that implement this interface and are annotated with {@link EventTrigger}
 * can be configured in YAML files. The event trigger value (e.g., "on-join",
 * "on-death") is used to match the event type in configuration files.
 * <p>
 * All events provide a {@link Resolver} through the {@link #resolve()} method
 * that can be used in action parameters for dynamic placeholders (e.g., "{arena}",
 * "{player}", "{killer}").
 *
 * @see ArenaEventManager
 * @see ArenaEventType
 * @see EventTrigger
 * @see org.clockworx.battlearena.event.action.EventAction
 */
public interface ArenaEvent extends Resolvable {

    /**
     * Gets the {@link Arena} this event is occurring in.
     *
     * @return the arena this event is occurring in
     */
    Arena getArena();

    /**
     * Gets the {@link Competition} this event is occurring in.
     *
     * @return the competition this event is occurring in
     */
    Competition<?> getCompetition();

    /**
     * Gets the {@link EventTrigger} annotation for this event, if present.
     * <p>
     * The event trigger annotation links the event class to a configuration
     * key (e.g., "on-join", "on-death") that can be used in arena YAML files.
     * Events without this annotation cannot be configured via YAML but can
     * still be used programmatically.
     *
     * @return the event trigger annotation, or {@code null} if not present
     */
    @Nullable
    default EventTrigger getEventTrigger() {
        if (this.getClass().isAnnotationPresent(EventTrigger.class)) {
            return this.getClass().getAnnotation(EventTrigger.class);
        }

        return null;
    }

    /**
     * Resolves this event to a {@link Resolver} containing context-specific values.
     * <p>
     * The resolver provides placeholders that can be used in action parameters
     * (e.g., messages, commands). By default, this includes:
     * <ul>
     *   <li>{@code {arena}} - The arena name</li>
     *   <li>{@code {competition}} - The competition/map name</li>
     * </ul>
     * <p>
     * Subclasses may override this method to provide additional resolvers
     * specific to their event type (e.g., {@code {player}}, {@code {killer}},
     * {@code {lives-left}}).
     *
     * @return a resolver containing event context values
     */
    default Resolver resolve() {
        return Resolver.builder()
                .define(ResolverKeys.ARENA, ResolverProvider.simple(this.getArena(), Arena::getName))
                .define(ResolverKeys.COMPETITION, ResolverProvider.simple(this.getCompetition(), c -> c.getMap().getName()))
                .build();
    }
}
