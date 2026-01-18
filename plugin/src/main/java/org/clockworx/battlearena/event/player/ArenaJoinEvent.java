package org.clockworx.battlearena.event.player;

import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.event.EventTrigger;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player joins a competition.
 * <p>
 * This event is triggered after a player successfully joins a competition and
 * their {@link org.clockworx.battlearena.ArenaPlayer} instance has been created.
 * <p>
 * Common actions configured for this event include:
 * <ul>
 *   <li>{@code store{types=all}} - Save the player's current state</li>
 *   <li>{@code change-gamemode{gamemode=adventure}} - Set game mode</li>
 *   <li>{@code teleport{location=waitroom}} - Move to waiting area</li>
 *   <li>{@code flight{enabled=false}} - Disable flight</li>
 * </ul>
 * <p>
 * Available resolver placeholders:
 * <ul>
 *   <li>{@code {player}} - Player's name</li>
 *   <li>{@code {arena}} - Arena name</li>
 *   <li>{@code {competition}} - Competition/map name</li>
 * </ul>
 *
 * @see ArenaLeaveEvent
 * @see ArenaSpectateEvent
 */
@EventTrigger("on-join")
public class ArenaJoinEvent extends BukkitArenaPlayerEvent {
    private final static HandlerList HANDLERS = new HandlerList();

    public ArenaJoinEvent(ArenaPlayer player) {
        super(player.getArena(), player);
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
