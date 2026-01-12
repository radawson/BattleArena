package mc.alk.arena.events.players;

import mc.alk.arena.objects.ArenaPlayer;
import mc.alk.arena.objects.arenas.Arena;
import mc.alk.arena.objects.pairs.JoinResult;
import mc.alk.arena.objects.joining.TeamJoinObject;
import mc.alk.arena.objects.teams.ArenaTeam;

/**
 * Event that is called when a player enters a queue
 *
 */
public class ArenaPlayerEnterQueueEvent extends ArenaPlayerEvent {

    final ArenaTeam team;
    final JoinResult result;
    final TeamJoinObject tqo;

    public ArenaPlayerEnterQueueEvent(ArenaPlayer player, ArenaTeam team, TeamJoinObject tqo, JoinResult queueResult) {
        super(player);
        this.team = team;
        this.result = queueResult;
        this.tqo = tqo;
    }

    /**
     * Returns the player's team
     *
     * @return The player's team
     */
    public ArenaTeam getTeam() {
        return team;
    }

    /**
     * Returns the queue result
     *
     * @return The queue result
     */
    public JoinResult getQueueResult(){
        return result;
    }

    /**
     * Returns the arena the player is joining
     *
     * @return The arena the player is joining
     */
    public Arena getArena(){
        return tqo.getJoinOptions().getArena();
    }
}
