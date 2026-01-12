package mc.alk.arena.events.players;

import mc.alk.arena.objects.ArenaPlayer;
import mc.alk.arena.objects.teams.ArenaTeam;

/**
 * Event that is called when a player leaves a match
 *
 * Signifies that the player has typed the command to Join the competition
 */
public class ArenaPlayerLeaveMatchEvent extends ArenaPlayerEvent {

    final ArenaTeam team;

    public ArenaPlayerLeaveMatchEvent(ArenaPlayer arenaPlayer, ArenaTeam team) {
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
