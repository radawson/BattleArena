package mc.alk.arena.events.teams;

import mc.alk.arena.events.CompetitionEvent;
import mc.alk.arena.objects.teams.ArenaTeam;

/**
 * Event that is called when everyone on a team dies
 *
 */
public class TeamDeathEvent extends CompetitionEvent {

    final ArenaTeam team;

    public TeamDeathEvent(ArenaTeam team) {
        this.team = team;
    }

    /**
     * Returns the team that died
     *
     * @return The team that died
     */
    public ArenaTeam getTeam() {
        return team;
    }
}
