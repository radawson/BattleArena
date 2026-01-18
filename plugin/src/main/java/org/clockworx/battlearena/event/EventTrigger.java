package org.clockworx.battlearena.event;

import org.bukkit.event.Event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that marks an {@link ArenaEvent} as configurable via arena YAML files.
 * <p>
 * When an event class is annotated with {@code @EventTrigger}, it can be referenced
 * in arena configuration files using the value specified in this annotation. For example,
 * an event annotated with {@code @EventTrigger("on-join")} can be configured in YAML
 * using the key {@code on-join:}.
 * <p>
 * Event triggers link event classes to their corresponding {@link ArenaEventType}.
 * When {@link ArenaEventManager#callEvent(org.bukkit.event.Event)} is called with
 * an event that has this annotation, the manager looks up the event type using
 * the trigger value and executes any configured actions.
 * <p>
 * The event must also have a corresponding {@link ArenaEventType} registered (either
 * a predefined constant or created via {@link ArenaEventType#create(String, Class)})
 * with the same name as the trigger value.
 *
 * @see ArenaEvent
 * @see ArenaEventType
 * @see ArenaEventManager
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventTrigger {

    /**
     * Gets the configuration key for this event trigger.
     * <p>
     * This value is used as the key in arena YAML files (e.g., "on-join", "on-death").
     * It must match the name of a registered {@link ArenaEventType}.
     *
     * @return the event trigger configuration key
     */
    String value();
}
