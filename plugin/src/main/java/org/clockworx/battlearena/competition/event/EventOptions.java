package org.clockworx.battlearena.competition.event;

import net.kyori.adventure.text.Component;
import org.clockworx.battlearena.config.ArenaOption;

import java.time.Duration;

/**
 * Represents the options for an event.
 */
public class EventOptions {
    @ArenaOption(name = "type", required = true, description = "The type of event.")
    private EventType type;
    @ArenaOption(name = "interval", required = true, description = "The interval of the event.")
    private Duration interval;
    @ArenaOption(name = "delay", description = "The delay until the event starts.")
    private Duration delay = Duration.ZERO;
    @ArenaOption(name = "message", required = true, description = "The message of the event.")
    private Component message;

    public EventOptions() {
    }

    public EventOptions(EventType type, Duration interval, Duration delay, Component message) {
        this.type = type;
        this.interval = interval;
        this.message = message;
    }

    public EventType getType() {
        return this.type;
    }

    public Duration getInterval() {
        return this.interval;
    }

    public Duration getDelay() {
        return this.delay;
    }

    public Component getMessage() {
        return this.message;
    }
}
