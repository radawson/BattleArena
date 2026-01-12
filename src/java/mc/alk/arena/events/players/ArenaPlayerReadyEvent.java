package mc.alk.arena.events.players;

import mc.alk.arena.objects.ArenaPlayer;

/**
 * Event that is called when a player readies in an arena
 *
 * Player has either typed command or clicked block to say they are ready
 */
public class ArenaPlayerReadyEvent extends ArenaPlayerEvent {
	boolean isReady;

	public ArenaPlayerReadyEvent(ArenaPlayer arenaPlayer, boolean isReady) {
		super(arenaPlayer);
		this.isReady = isReady;
	}

	/**
	 * Returns if the player is ready
	 *
	 * @return If the player is ready
	 */
	public boolean isReady(){
		return isReady;
	}
}
