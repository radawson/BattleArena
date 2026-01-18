package org.clockworx.battlearena.event;

import org.clockworx.battlearena.config.DocumentationSource;
import org.clockworx.battlearena.event.arena.ArenaDrawEvent;
import org.clockworx.battlearena.event.arena.ArenaLoseEvent;
import org.clockworx.battlearena.event.arena.ArenaPhaseCompleteEvent;
import org.clockworx.battlearena.event.arena.ArenaPhaseStartEvent;
import org.clockworx.battlearena.event.arena.ArenaVictoryEvent;
import org.clockworx.battlearena.event.player.ArenaDeathEvent;
import org.clockworx.battlearena.event.player.ArenaJoinEvent;
import org.clockworx.battlearena.event.player.ArenaKillEvent;
import org.clockworx.battlearena.event.player.ArenaLeaveEvent;
import org.clockworx.battlearena.event.player.ArenaLifeDepleteEvent;
import org.clockworx.battlearena.event.player.ArenaLivesExhaustEvent;
import org.clockworx.battlearena.event.player.ArenaRespawnEvent;
import org.clockworx.battlearena.event.player.ArenaSpectateEvent;
import org.clockworx.battlearena.event.player.ArenaStatChangeEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents an event type in an arena.
 * <p>
 * Event types link event classes to configuration keys used in arena YAML files.
 * Each event type has a name (e.g., "on-join", "on-death") that corresponds to
 * the key used in the {@code events:} section of arena configuration.
 * <p>
 * When an event is triggered via {@link ArenaEventManager#callEvent(org.bukkit.event.Event)},
 * the event's {@link EventTrigger} annotation is used to look up the corresponding
 * {@code ArenaEventType}, which then determines which actions should be executed.
 * <p>
 * Predefined event types are available as static constants (e.g., {@link #ON_JOIN},
 * {@link #ON_DEATH}, {@link #ON_VICTORY}). Custom event types can be created using
 * {@link #create(String, Class)}.
 *
 * @param <T> the type of event this event type represents
 * @see ArenaEvent
 * @see EventTrigger
 * @see ArenaEventManager
 */
@DocumentationSource("https://docs.battleplugins.org/books/user-guide/page/event-reference")
public final class ArenaEventType<T extends ArenaEvent> {
    private static final Map<String, ArenaEventType<?>> EVENT_TYPES = new HashMap<>();

    public static final ArenaEventType<ArenaPhaseCompleteEvent> ON_COMPLETE = new ArenaEventType<>("on-complete", ArenaPhaseCompleteEvent.class);
    public static final ArenaEventType<ArenaDeathEvent> ON_DEATH = new ArenaEventType<>("on-death", ArenaDeathEvent.class);
    public static final ArenaEventType<ArenaDrawEvent> ON_DRAW = new ArenaEventType<>("on-draw", ArenaDrawEvent.class);
    public static final ArenaEventType<ArenaKillEvent> ON_KILL = new ArenaEventType<>("on-kill", ArenaKillEvent.class);
    public static final ArenaEventType<ArenaJoinEvent> ON_JOIN = new ArenaEventType<>("on-join", ArenaJoinEvent.class);
    public static final ArenaEventType<ArenaLeaveEvent> ON_LEAVE = new ArenaEventType<>("on-leave", ArenaLeaveEvent.class);
    public static final ArenaEventType<ArenaLifeDepleteEvent> ON_LIFE_DEPLETE = new ArenaEventType<>("on-life-deplete", ArenaLifeDepleteEvent.class);
    public static final ArenaEventType<ArenaLivesExhaustEvent> ON_LIVES_EXHAUST = new ArenaEventType<>("on-lives-exhaust", ArenaLivesExhaustEvent.class);
    public static final ArenaEventType<ArenaLoseEvent> ON_LOSE = new ArenaEventType<>("on-lose", ArenaLoseEvent.class);
    public static final ArenaEventType<ArenaRespawnEvent> ON_RESPAWN = new ArenaEventType<>("on-respawn", ArenaRespawnEvent.class);
    public static final ArenaEventType<ArenaSpectateEvent> ON_SPECTATE = new ArenaEventType<>("on-spectate", ArenaSpectateEvent.class);
    public static final ArenaEventType<ArenaPhaseStartEvent> ON_START = new ArenaEventType<>("on-start", ArenaPhaseStartEvent.class);
    public static final ArenaEventType<ArenaStatChangeEvent> ON_STAT_CHANGE = new ArenaEventType<>("on-stat-change", ArenaStatChangeEvent.class);
    public static final ArenaEventType<ArenaVictoryEvent> ON_VICTORY = new ArenaEventType<>("on-victory", ArenaVictoryEvent.class);

    /**
     * The configuration name for this event type (e.g., "on-join", "on-death").
     */
    private final String name;
    
    /**
     * The event class this type represents.
     */
    private final Class<T> clazz;

    /**
     * Creates a new event type and registers it in the global registry.
     *
     * @param name the configuration name for the event type
     * @param clazz the event class
     */
    ArenaEventType(String name, Class<T> clazz) {
        this.name = name;
        this.clazz = clazz;

        EVENT_TYPES.put(name, this);
    }

    /**
     * Gets the configuration name for this event type.
     * <p>
     * This is the key used in arena YAML files (e.g., "on-join", "on-death").
     *
     * @return the event type name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the event class this type represents.
     *
     * @return the event class
     */
    public Class<T> getEventType() {
        return this.clazz;
    }

    /**
     * Looks up an event type by its configuration name.
     *
     * @param name the event type name (e.g., "on-join", "on-death")
     * @return the event type, or {@code null} if not found
     */
    @Nullable
    public static ArenaEventType<?> get(String name) {
        return EVENT_TYPES.get(name);
    }

    /**
     * Creates a new custom event type.
     * <p>
     * This allows plugins to register custom event types that can be used
     * in arena configuration files. The event class should be annotated with
     * {@link EventTrigger} using the same name.
     *
     * @param name the configuration name for the event type
     * @param clazz the event class
     * @param <E> the event type
     * @return a new event type instance
     */
    public static <E extends ArenaEvent> ArenaEventType<E> create(String name, Class<E> clazz) {
        return new ArenaEventType<>(name, clazz);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ArenaEventType<?> that = (ArenaEventType<?>) object;
        return Objects.equals(this.clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.clazz);
    }

    /**
     * Gets all registered event types.
     *
     * @return an immutable set of all event types
     */
    public static Set<ArenaEventType<?>> values() {
        return Set.copyOf(EVENT_TYPES.values());
    }
}
