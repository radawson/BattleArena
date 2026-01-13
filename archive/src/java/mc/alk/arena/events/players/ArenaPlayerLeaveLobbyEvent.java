package mc.alk.arena.events.players;

import mc.alk.arena.objects.ArenaPlayer;
import mc.alk.arena.objects.teams.ArenaTeam;

/**
 * Event that is called when a player leaves the lobby
 *
 */
public class ArenaPlayerLeaveLobbyEvent extends ArenaPlayerEvent {

    final ArenaTeam team;

    public ArenaPlayerLeaveLobbyEvent(ArenaPlayer arenaPlayer, ArenaTeam team) {
        super(arenaPlayer);
        this.team = team;
    }

    /**
     * Returns the team the player who died is on
     *
     * @return The team the player who died is on
     */
    public ArenaTeam getTeam() {
        return team;
    }

}
