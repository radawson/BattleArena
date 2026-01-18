package org.clockworx.battlearena.event.player;

import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.event.EventTrigger;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player respawns after death in a competition.
 * <p>
 * This event is triggered after a player has been respawned. It can be used
 * to give items, apply effects, or teleport the player to a specific location.
 * <p>
 * Common actions configured for this event include:
 * <ul>
 *   <li>{@code give-item{item=...}} - Give starting items</li>
 *   <li>{@code teleport{location=team_spawn}} - Move to spawn</li>
 *   <li>{@code give-effects{effects=[...]}} - Apply effects</li>
 * </ul>
 * <p>
 * Available resolver placeholders:
 * <ul>
 *   <li>{@code {player}} - Player's name</li>
 *   <li>{@code {arena}} - Arena name</li>
 *   <li>{@code {competition}} - Competition/map name</li>
 * </ul>
 *
 * @see ArenaDeathEvent
 */
@EventTrigger("on-respawn")
public class ArenaRespawnEvent extends BukkitArenaPlayerEvent {
    private final static HandlerList HANDLERS = new HandlerList();

    public ArenaRespawnEvent(ArenaPlayer player) {
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
