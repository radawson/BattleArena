package mc.alk.arena.events.teams;

import mc.alk.arena.events.BAEvent;
import mc.alk.arena.objects.MatchParams;
import mc.alk.arena.objects.teams.ArenaTeam;

/**
 * Event that is called when a team leaves a queue
 *
 */
public class TeamLeftQueueEvent extends BAEvent {

    final ArenaTeam team;
    MatchParams params;

    public TeamLeftQueueEvent(ArenaTeam team, MatchParams params) {
        this.team = team;
        this.params = params;
    }

    /**
     * Returns the team that left the queue
     *
     * @return The team that left the queue
     */
    public ArenaTeam getTeam(){
        return team;
    }

    /**
     * Returns the match parameters
     *
     * @return The match parameters
     */
    public MatchParams getParams(){
        return params;
    }
}
