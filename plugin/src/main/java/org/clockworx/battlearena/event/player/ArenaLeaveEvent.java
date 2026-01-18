package org.clockworx.battlearena.event.player;

import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.event.EventTrigger;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player leaves a competition.
 * <p>
 * This event is triggered when a player leaves a competition for any reason.
 * The {@link #getCause()} method indicates why the player left.
 * <p>
 * Common actions configured for this event include:
 * <ul>
 *   <li>{@code clear-effects} - Remove potion effects</li>
 *   <li>{@code restore{types=all}} - Restore saved player state</li>
 *   <li>{@code remove-scoreboard} - Remove scoreboard display</li>
 * </ul>
 * <p>
 * Available resolver placeholders:
 * <ul>
 *   <li>{@code {player}} - Player's name</li>
 *   <li>{@code {arena}} - Arena name</li>
 *   <li>{@code {competition}} - Competition/map name</li>
 * </ul>
 *
 * @see ArenaJoinEvent
 * @see Cause
 */
@EventTrigger("on-leave")
public class ArenaLeaveEvent extends BukkitArenaPlayerEvent {
    private final static HandlerList HANDLERS = new HandlerList();

    private final Cause cause;

    public ArenaLeaveEvent(ArenaPlayer player, Cause cause) {
        super(player.getArena(), player);

        this.cause = cause;
    }

    /**
     * Gets the cause of the player leaving the arena.
     *
     * @return the cause of the player leaving the arena
     */
    public Cause getCause() {
        return this.cause;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * The cause of the player leaving the arena.
     */
    public enum Cause {
        /**
         * The player left the arena by command.
         */
        COMMAND,
        /**
         * The player left the arena by disconnection.
         */
        DISCONNECT,
        /**
         * The player left the arena due to the game
         * kicking them out.
         */
        GAME,
        /**
         * The player left the arena due to the server
         * or game shutting down.
         */
        SHUTDOWN,
        /**
         * The plugin caused the player to leave the arena.
         */
        PLUGIN,
        /**
         * The player left the arena due to being kicked by
         * an administrator.
         */
        KICKED,
        /**
         * The competition was forcefully removed from the
         * arena.
         */
        REMOVED
    }
}
