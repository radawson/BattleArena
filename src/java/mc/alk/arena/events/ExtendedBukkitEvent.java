package mc.alk.arena.events;

import org.bukkit.event.Event;

/**
 * Class that holds bukkit event info
 */
public abstract class ExtendedBukkitEvent {

    protected Event event;

    /**
     * Returns the bukkit event
     *
     * @return The bukkit event
     */
    public abstract Event getBukkitEvent();
}
