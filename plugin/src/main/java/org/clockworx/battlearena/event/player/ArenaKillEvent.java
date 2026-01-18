package org.clockworx.battlearena.event.player;

import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.event.EventTrigger;
import org.clockworx.battlearena.resolver.Resolver;
import org.clockworx.battlearena.resolver.ResolverKeys;
import org.clockworx.battlearena.resolver.ResolverProvider;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player kills another player in a competition.
 * <p>
 * This event is triggered when one player eliminates another player.
 * The event provides access to both the killer (via {@link #getKiller()})
 * and the killed player (via {@link #getKilled()}).
 * <p>
 * Common actions configured for this event include:
 * <ul>
 *   <li>{@code send-message{message=...}} - Notify the killer</li>
 *   <li>{@code play-sound{...}} - Play victory sound</li>
 *   <li>{@code give-item{item=...}} - Reward the killer</li>
 *   <li>{@code broadcast{message=...}} - Announce to all players</li>
 * </ul>
 * <p>
 * Available resolver placeholders:
 * <ul>
 *   <li>{@code {player}} or {@code {killer}} - Killer's name</li>
 *   <li>{@code {killed}} - Killed player's name</li>
 *   <li>{@code {arena}} - Arena name</li>
 *   <li>{@code {competition}} - Competition/map name</li>
 * </ul>
 *
 * @see ArenaDeathEvent
 */
@EventTrigger("on-kill")
public class ArenaKillEvent extends BukkitArenaPlayerEvent {
    private final static HandlerList HANDLERS = new HandlerList();

    private final ArenaPlayer killed;

    public ArenaKillEvent(ArenaPlayer player, ArenaPlayer killed) {
        super(player.getArena(), player);

        this.killed = killed;
    }

    /**
     * Returns the player that was killed.
     *
     * @return the player that was killed
     */
    public ArenaPlayer getKilled() {
        return this.killed;
    }

    /**
     * Returns the player that killed the other player.
     *
     * @return the player that killed the other player
     */
    public ArenaPlayer getKiller() {
        return this.getArenaPlayer();
    }

    @Override
    public Resolver resolve() {
        return super.resolve()
                .toBuilder()
                .define(ResolverKeys.KILLER, ResolverProvider.simple(this.getKiller(), this.getKiller().getPlayer()::getName))
                .define(ResolverKeys.KILLED, ResolverProvider.simple(this.getKilled(), this.getKilled().getPlayer()::getName))
                .build();
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
