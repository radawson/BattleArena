package org.clockworx.battlearena.event.player;

import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.event.EventTrigger;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player dies in a competition.
 * <p>
 * This event is triggered when a player's health reaches zero. It fires
 * before the player is respawned, allowing actions to be configured for
 * death handling (e.g., clearing inventory, respawning, teleporting).
 * <p>
 * Common actions configured for this event include:
 * <ul>
 *   <li>{@code clear-inventory} - Remove all items</li>
 *   <li>{@code respawn} - Respawn the player</li>
 *   <li>{@code delay{ticks=20}} - Wait before next action</li>
 *   <li>{@code teleport{location=waitroom}} - Move to waiting area</li>
 * </ul>
 * <p>
 * Available resolver placeholders:
 * <ul>
 *   <li>{@code {player}} - Player's name</li>
 *   <li>{@code {arena}} - Arena name</li>
 *   <li>{@code {competition}} - Competition/map name</li>
 * </ul>
 *
 * @see ArenaKillEvent
 * @see ArenaRespawnEvent
 */
@EventTrigger("on-death")
public class ArenaDeathEvent extends BukkitArenaPlayerEvent {
    private final static HandlerList HANDLERS = new HandlerList();

    public ArenaDeathEvent(ArenaPlayer player) {
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
