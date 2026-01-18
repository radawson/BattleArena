package org.clockworx.battlearena.event.player;

import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.event.EventTrigger;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player enters spectator mode in a competition.
 * <p>
 * This event is triggered when a player's role changes to spectating.
 * Common actions include saving state, changing game mode, enabling flight,
 * and teleporting to spectator areas.
 * <p>
 * Available resolver placeholders:
 * <ul>
 *   <li>{@code {player}} - Player's name</li>
 *   <li>{@code {arena}} - Arena name</li>
 *   <li>{@code {competition}} - Competition/map name</li>
 * </ul>
 *
 * @see ArenaJoinEvent
 * @see org.clockworx.battlearena.event.action.types.ChangeRoleAction
 */
@EventTrigger("on-spectate")
public class ArenaSpectateEvent extends BukkitArenaPlayerEvent {
    private final static HandlerList HANDLERS = new HandlerList();

    public ArenaSpectateEvent(ArenaPlayer player) {
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
