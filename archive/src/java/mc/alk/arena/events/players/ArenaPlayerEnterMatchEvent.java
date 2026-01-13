package mc.alk.arena.events.players;

import mc.alk.arena.objects.ArenaPlayer;
import mc.alk.arena.objects.teams.ArenaTeam;

/**
 * Event that is called when a player enters a match
 *
 * Signifies that the player has typed the command to join the competition
 */
public class ArenaPlayerEnterMatchEvent extends ArenaPlayerEvent {
    final ArenaTeam team;

    public ArenaPlayerEnterMatchEvent(ArenaPlayer arenaPlayer, ArenaTeam team) {
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
