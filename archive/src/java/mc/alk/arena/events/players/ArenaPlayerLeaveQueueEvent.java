package mc.alk.arena.events.players;

import mc.alk.arena.objects.ArenaPlayer;
import mc.alk.arena.objects.MatchParams;
import mc.alk.arena.objects.arenas.Arena;
import mc.alk.arena.objects.joining.ArenaMatchQueue;

/**
 * Event that is called when a player leaves a queue
 *
 */
public class ArenaPlayerLeaveQueueEvent extends ArenaPlayerEvent {

//    final ArenaTeam team;
    final MatchParams params;
    final Arena arena;

    public ArenaPlayerLeaveQueueEvent(ArenaPlayer arenaPlayer, MatchParams params, Arena arena) {
        super(arenaPlayer);
        this.params = params;
        this.arena = arena;
    }

//    public ArenaTeam getTeam() {
//        return team;
//    }

    /**
     * Returns the match parameters
     *
     * @return The match parameters
     */
    public MatchParams getParams() {
        return params;
    }

    /**
     * Returns the arena the player is leaving
     *
     * @return The arena the player is leaving
     */
    public Arena getArena() {
        return arena;
    }

    /**
     * Returns the number of players in the arena queue
     *
     * @return the number of players in the arena queue
     */
    public int getPlayersInArenaQueue(Arena arena) {
        return ArenaMatchQueue.getPlayersInArenaQueue(arena);
    }
//
//	public int getNPlayers(){
//		return ptp.getNPlayersInQueue();
//	}
}
