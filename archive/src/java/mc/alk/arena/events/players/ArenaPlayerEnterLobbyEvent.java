package mc.alk.arena.events.players;

import mc.alk.arena.objects.ArenaPlayer;
import mc.alk.arena.objects.teams.ArenaTeam;

/**
 * Event that is called when a player enters a lobby
 *
 */
public class ArenaPlayerEnterLobbyEvent extends ArenaPlayerEvent {
    final ArenaTeam team;

    public ArenaPlayerEnterLobbyEvent(ArenaPlayer arenaPlayer, ArenaTeam team) {
        super(arenaPlayer);
        this.team = team;
    }

    /**
     * Returns the player's team
     *
     * @return The player's team
     */
    public ArenaTeam getTeam() {
        return team;
    }

}
